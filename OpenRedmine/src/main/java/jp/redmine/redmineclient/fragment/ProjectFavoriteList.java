package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.FavoriteProjectListAdapter;
import jp.redmine.redmineclient.adapter.ProjectListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.provider.Project;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProjectFavoriteList extends OrmLiteFragment<DatabaseCacheHelper> {
	private static final String TAG = ProjectFavoriteList.class.getSimpleName();
	private IssueActionInterface mListener;
	private StickyListHeadersListView list;

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

		final FavoriteProjectListAdapter adapter = new FavoriteProjectListAdapter(getActivity(), null, true);

		getLoaderManager().initLoader(0, getArguments(), new LoaderManager.LoaderCallbacks<Cursor>() {

			@Override
			public Loader<Cursor> onCreateLoader(int id, Bundle args) {

				ConnectionArgument intent = new ConnectionArgument();
				intent.setArgument(args);
				return new CursorLoader(getActivity()
						, Uri.parse(Project.PROVIDER_BASE)
						, null, RedmineProject.FAVORITE + " = 1", null, RedmineProject.CONNECTION);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
				adapter.swapCursor(data);
				list.setAdapter(adapter);
			}

			@Override
			public void onLoaderReset(Loader<Cursor> loader) {
				adapter.swapCursor(null);
			}

		});

		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				Object item = adapterView.getItemAtPosition(i);
				if (item == null || !(item instanceof Cursor))
					return false;
				mListener.onKanbanList(ProjectListAdapter.getConnectionId((Cursor) item), ProjectListAdapter.getProjectId((Cursor) item));
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
				Object item = adapterView.getItemAtPosition(position);
				if (item == null || !(item instanceof Cursor))
					return;
				mListener.onIssueList(ProjectListAdapter.getConnectionId((Cursor) item), ProjectListAdapter.getProjectId((Cursor) item));
			}
		});
		return current;
	}
}
