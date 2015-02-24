package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.database.Cursor;
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
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectContract;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProjectFavoriteList extends OrmLiteFragment<DatabaseCacheHelper> implements
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
		adapter.notifyDataSetChanged();

		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				Object item =  adapterView.getItemAtPosition(i);
				if(item == null || !(item instanceof RedmineProject))
					return false;
				RedmineProject project = (RedmineProject)item;
				mListener.onKanbanList(project.getConnectionId(), project.getId());
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
				if(item == null || !(item instanceof RedmineProject))
					return;
				RedmineProject project = (RedmineProject)item;
				mListener.onIssueList(project.getConnectionId(), project.getId());
			}
		});
		return current;
	}
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),
				RedmineProjectContract.CONTENT_URI, null
				, RedmineProjectContract.FAVORITE + ">0"
				, null, null);
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
