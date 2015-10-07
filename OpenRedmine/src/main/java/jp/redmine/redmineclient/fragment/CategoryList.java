package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.CategoryListAdapter;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.ProjectArgument;
import jp.redmine.redmineclient.provider.Category;

public class CategoryList extends ListFragment {
	private static final String TAG = CategoryList.class.getSimpleName();
	private CategoryListAdapter adapter;
	private IssueActionInterface mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = ActivityHandler.getHandler(activity, IssueActionInterface.class);

	}
	public CategoryList(){
		super();
	}

	static public CategoryList newInstance(ProjectArgument arg){
		CategoryList fragment = new CategoryList();
		fragment.setArguments(arg.getArgument());
		return fragment;
	}

	@Override
	public void onDestroyView() {
		setListAdapter(null);
		super.onDestroyView();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().setFastScrollEnabled(true);

		adapter = new CategoryListAdapter(getActivity(), null, true);

		getLoaderManager().initLoader(0, getArguments(), new LoaderManager.LoaderCallbacks<Cursor>() {

			@Override
			public Loader<Cursor> onCreateLoader(int id, Bundle args) {

				ProjectArgument intent = new ProjectArgument();
				intent.setArgument(args);
				return new CursorLoader(getActivity()
						, Uri.parse(Category.PROVIDER_BASE + "/project/" + String.valueOf(intent.getProjectId()))
						, null, null, null, null);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
				adapter.swapCursor(data);
				setListAdapter(adapter);
			}

			@Override
			public void onLoaderReset(Loader<Cursor> loader) {
				adapter.swapCursor(null);
			}

		});

	}

	@Override
	public void onResume() {
		super.onResume();
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView listView, View v, int position, long id) {
		super.onListItemClick(listView, v, position, id);
		Object listitem = listView.getItemAtPosition(position);
		if(listitem == null || !Cursor.class.isInstance(listitem)  )
		{
			return;
		}
		Cursor item = (Cursor) listitem;
		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());
		mListener.onIssueList(intent.getConnectionId(), intent.getProjectId(),
				"category", CategoryListAdapter.getId(item));

	}

}
