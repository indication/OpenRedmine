package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.FavoriteProjectListAdapter;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProjectFavoriteList extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = ProjectFavoriteList.class.getSimpleName();
	private IssueActionInterface mListener;
	private StickyListHeadersListView list;
	private FavoriteProjectListAdapter adapter;

	public ProjectFavoriteList(){
		super();
	}

	static public ProjectFavoriteList newInstance(){
		ProjectFavoriteList instance = new ProjectFavoriteList();
		return instance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = ActivityHandler.getHandler(activity, IssueActionInterface.class);
	}

	@Override
	public void onDestroyView() {
		if(list  != null)
			list.setAdapter(null);
		super.onDestroyView();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		list.setFastScrollEnabled(true);

		adapter = new FavoriteProjectListAdapter(getActivity(), null, true);

		getLoaderManager().initLoader(0, null, this);

		list.setAdapter(adapter);

		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				Object item =  adapterView.getItemAtPosition(i);
				if(item == null || !(item instanceof Cursor))
					return false;
				Cursor project = (Cursor)item;
				mListener.onKanbanList(FavoriteProjectListAdapter.getConnectionId(project), FavoriteProjectListAdapter.getId(project));
				return true;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View current = inflater.inflate(R.layout.page_stickylistheaderslist, container, false);
		list = (StickyListHeadersListView)current.findViewById(R.id.list);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Object item =  adapterView.getItemAtPosition(position);
				if(item == null || !(item instanceof Cursor))
					return;
				Cursor project = (Cursor)item;
				mListener.onIssueList(FavoriteProjectListAdapter.getConnectionId(project), FavoriteProjectListAdapter.getId(project));
			}
		});
		return current;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return FavoriteProjectListAdapter.getCursorLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
}
