package jp.redmine.redmineclient.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ListFragmentSwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.ConnectionActionInterface;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.ProjectListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectContract;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.task.SelectProjectTask;

public class ProjectList extends OrmLiteListFragment<DatabaseCacheHelper> implements
		LoaderManager.LoaderCallbacks<Cursor>
		,SwipeRefreshLayout.OnRefreshListener
{
	private static final String TAG = ProjectList.class.getSimpleName();
	private ProjectListAdapter adapter;
	private SelectDataTask task;
	private MenuItem menu_refresh;
	private View mFooter;
	private IssueActionInterface mListener;
	private ConnectionActionInterface mConnectionListener;
	SwipeRefreshLayout mSwipeRefreshLayout;

	public ProjectList(){
		super();
	}

	static public ProjectList newInstance(ConnectionArgument intent){
		ProjectList instance = new ProjectList();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = ActivityHandler.getHandler(activity, IssueActionInterface.class);
		mConnectionListener = ActivityHandler.getHandler(activity, ConnectionActionInterface.class);
	}

	@Override
	public void onDestroyView() {
		cancelTask();
		setListAdapter(null);
		super.onDestroyView();
	}
	protected void cancelTask(){
		// cleanup task
		if(task != null && task.getStatus() == Status.RUNNING){
			task.cancel(true);
		}
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().addFooterView(mFooter);
		getListView().setFastScrollEnabled(true);
		getListView().setTextFilterEnabled(true);


		adapter = new ProjectListAdapter(getActivity(), null, true);

		getLoaderManager().initLoader(0, null, this);

		setListAdapter(adapter);
		adapter.notifyDataSetChanged();

		if(adapter.getCount() < 1){
			onRefresh();
		}
		adapter.setOnFavoriteClickListener(new ProjectListAdapter.OnFavoriteClickListener() {
			@Override
			public void onItemClick(View view, int position, int id, boolean b) {
				if(adapter == null)
					return;
				ProjectListAdapter.updateFavorite(getActivity().getContentResolver(), id, b);
			}
		});

		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFooter = inflater.inflate(R.layout.listview_footer,null);
		mFooter.setVisibility(View.GONE);
		View view = super.onCreateView(inflater, container, savedInstanceState);
		ListFragmentSwipeRefreshLayout.ViewRefreshLayout result
				= ListFragmentSwipeRefreshLayout.inject(container, view);
		mSwipeRefreshLayout = result.layout;
		mSwipeRefreshLayout.setOnRefreshListener(this);
		return result.parent;
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
		ConnectionArgument intent = new ConnectionArgument();
		intent.setArgument(getArguments());
		int connection_id = intent.getConnectionId();
		mListener.onIssueList(connection_id, id);
	}

	public void onRefresh(){
		if(task != null && task.getStatus() == Status.RUNNING){
			return;
		}
		ConnectionArgument intent = new ConnectionArgument();
		intent.setArgument(getArguments());
		RedmineConnection connection = ConnectionModel.getConnectionItem(getActivity().getContentResolver(), intent.getConnectionId());
		task = new SelectDataTask(getHelper());
		task.execute(connection);
	}

	private class SelectDataTask extends SelectProjectTask {
		public SelectDataTask(DatabaseCacheHelper helper) {
			super(helper);
		}

		// can use UI thread here
		@Override
		protected void onPreExecute() {
			mFooter.setVisibility(View.VISIBLE);
			if(menu_refresh != null)
				menu_refresh.setEnabled(false);
			if(mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
				mSwipeRefreshLayout.setRefreshing(true);
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(Void b) {
			mFooter.setVisibility(View.GONE);
			adapter.notifyDataSetChanged();
			if(menu_refresh != null)
				menu_refresh.setEnabled(true);
			if(mSwipeRefreshLayout != null)
				mSwipeRefreshLayout.setRefreshing(false);
		}

		@Override
		protected void onProgress(int max, int proc) {
			adapter.notifyDataSetChanged();
			super.onProgress(max, proc);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate( R.menu.projects, menu );
		menu_refresh = menu.findItem(R.id.menu_refresh);
		if(task != null && task.getStatus() == Status.RUNNING)
			menu_refresh.setEnabled(false);

		setupSearchBar(menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected void setupSearchBar(Menu menu){

		SearchView search = new SearchView(getActivity());
		search.setIconifiedByDefault(false);
		search.setSubmitButtonEnabled(true);
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				if (TextUtils.isDigitsOnly(s)){
					ConnectionArgument intent = new ConnectionArgument();
					intent.setArgument(getArguments());
					mListener.onIssueSelected(intent.getConnectionId(), Integer.parseInt(s));
					return true;
				} else {
					return onQueryTextChange(s);
				}
			}

			@Override
			public boolean onQueryTextChange(String s) {
				if (TextUtils.isEmpty(s)) {
					getListView().clearTextFilter();
				} else {
					getListView().setFilterText(s);
				}
				return true;
			}
		});
		menu.add(android.R.string.search_go)
				.setIcon(android.R.drawable.ic_menu_search)
				.setActionView(search)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
		;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_refresh:
			{
				this.onRefresh();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		ConnectionArgument intent = new ConnectionArgument();
		intent.setArgument(getArguments());
		int connection_id = intent.getConnectionId();
		return new CursorLoader(getActivity(),
			RedmineProjectContract.CONTENT_URI, null
			, RedmineProjectContract.CONNECTION_ID + "=?"
			, new String[]{
				String.valueOf(connection_id)
			}, null);
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
