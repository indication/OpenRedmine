package jp.redmine.redmineclient.fragment;

import java.sql.SQLException;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.widget.SearchView;
import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.FilterViewActivity;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.IssueActionEmptyHandler;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.RedmineIssueListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.RedmineIssueFilterHeader;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.FilterArgument;
import jp.redmine.redmineclient.param.ProjectArgument;
import jp.redmine.redmineclient.task.SelectIssueTask;
import jp.redmine.redmineclient.task.SelectProjectEnumerationTask;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class IssueList extends OrmLiteListFragment<DatabaseCacheHelper> {
	private static final String TAG = IssueList.class.getSimpleName();
	private static final int ACTIVITY_FILTER = 2001;
	private RedmineIssueListAdapter adapter;
	private SelectDataTask task;
	private MenuItem menu_refresh;
	private View mFooter;
	private View mHeader;
	private long lastPos = -1;
	private boolean isBlockFetch = false;

	private IssueActionInterface mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof ActivityInterface){
			mListener = ((ActivityInterface)activity).getHandler( IssueActionInterface.class);
		}
		if(mListener == null) {
			//setup empty events
			mListener = new  IssueActionEmptyHandler();
		}

	}
	public IssueList(){
		super();
	}

	static public IssueList newInstance(ProjectArgument arg){
		IssueList fragment = new IssueList();
		fragment.setArguments(arg.getArgument());
		return fragment;
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
		getListView().addHeaderView(mHeader);

		getListView().setFastScrollEnabled(true);
		getListView().setTextFilterEnabled(true);

		adapter = new RedmineIssueListAdapter(getHelper(), getActivity());
		FilterArgument intent = new FilterArgument();
		intent.setArgument( getArguments() );
		if(intent.hasFilterId()){
			adapter.setupParameter(intent.getFilterId());
		} else {
			adapter.setupParameter(intent.getConnectionId(),intent.getProjectId());
		}
		onRefreshList();
		if(adapter.getCount() < 1){
			this.onRefresh(true);
		}
		setListAdapter(adapter);

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3) {
				ListView listView = (ListView) parent;
				Object listitem = listView.getItemAtPosition(position);
				if(listitem == null || ! RedmineIssue.class.isInstance(listitem)  )
				{
					return false;
				}
				RedmineIssue item = (RedmineIssue) listitem;
				mListener.onIssueEdit(item.getConnectionId(), item.getIssueId());
				return true;
			}
		});

		getListView().setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
			                     int visibleItemCount, int totalItemCount) {
				if (totalItemCount == firstVisibleItem + visibleItemCount) {
					if(task != null && task.getStatus() == Status.RUNNING)
						return;
					if(lastPos == totalItemCount)
						return;
					onRefresh(false);
					lastPos = totalItemCount;
				}
			}
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {

			}
		});
		isBlockFetch = false;
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
		mHeader = inflater.inflate(R.layout.filterheader,null);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private PullToRefreshLayout mPullToRefreshLayout;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// This is the View which is created by ListFragment
		ViewGroup viewGroup = (ViewGroup) view;

		// We need to create a PullToRefreshLayout manually
		mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

		// We can now setup the PullToRefreshLayout
		ActionBarPullToRefresh.from(getActivity())
			// We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
			.insertLayoutInto(viewGroup)

			// We need to mark the ListView and it's Empty View as pullable
			// This is because they are not dirent children of the ViewGroup
			.theseChildrenArePullable(android.R.id.list, android.R.id.empty)

			// We can now complete the setup as desired
			.listener(new OnRefreshListener() {
				@Override
				public void onRefreshStarted(View view) {
					onRefresh(true);
				}
			})
			.setup(mPullToRefreshLayout);
	}

	@Override
	public void onResume() {
		super.onResume();
		onRefreshList();
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		super.onListItemClick(parent, v, position, id);
		if(v == mHeader){
			if(adapter != null && adapter.getParameter() != null && adapter.getParameter().isCurrent() == true)
				intentFilterAction();
			return;
		}
		Object listitem = parent.getItemAtPosition(position);
		if(listitem == null || ! RedmineIssue.class.isInstance(listitem)  )
		{
			return;
		}
		RedmineIssue item = (RedmineIssue) listitem;
		mListener.onIssueSelected(item.getConnectionId(), item.getIssueId());
	}

	protected void onRefresh(boolean isFlush){
		if(task != null && task.getStatus() == Status.RUNNING){
			return;
		}
		onRefreshList();
		if(isBlockFetch)
			return;
		if(lastPos != getListView().getChildCount()){
			lastPos = -1; //reset
		}

		FilterArgument intent = new FilterArgument();
		intent.setArgument(getArguments());
		DatabaseCacheHelper helper = getHelper();
		ConnectionModel mConnection = new ConnectionModel(getActivity());
		RedmineConnection connection = mConnection.getItem(intent.getConnectionId());
		mConnection.finalize();

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if(intent.hasFilterId())
			task = new SelectDataTask(helper,connection,null,intent.getFilterId());
		else
			task = new SelectDataTask(helper,connection,intent.getProjectId());

		task.setFetchAll(sp.getBoolean("issue_get_all", false));
		task.execute(0,10,isFlush ? 1 : 0);
		if(isFlush && !intent.hasFilterId()){
			RedmineProject project = null;
			RedmineProjectModel mProject = new RedmineProjectModel(helper);
			try {
				project = mProject.fetchById(intent.getProjectId());
			} catch (SQLException e) {
				Log.e(TAG,"SelectDataTask",e);
			}
			SelectProjectEnumerationTask enumtask = new SelectProjectEnumerationTask(helper,connection,project);
			enumtask.execute();
		}
	}
	protected void onRefreshList(){
		if(adapter == null)
			return;
		adapter.notifyDataSetChanged();
		RedmineIssueFilterHeader form = new RedmineIssueFilterHeader(mHeader);
		form.setValue(adapter.getParameter());

	}

	private class SelectDataTask extends SelectIssueTask {
		public SelectDataTask(DatabaseCacheHelper helper,RedmineConnection connection, long project) {
			super(helper,connection,project);
		}
		public SelectDataTask(DatabaseCacheHelper helper,RedmineConnection connection,Long proj, int filter) {
			super(helper,connection,proj,filter);
		}

		// can use UI thread here
		@Override
		protected void onPreExecute() {
			mFooter.setVisibility(View.VISIBLE);
			if(menu_refresh != null)
				menu_refresh.setEnabled(false);
			if(mPullToRefreshLayout != null && !mPullToRefreshLayout.isRefreshing())
				mPullToRefreshLayout.setRefreshing(true);
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(Void b) {
			mFooter.setVisibility(View.GONE);
			onRefreshList();
			if(menu_refresh != null)
				menu_refresh.setEnabled(true);
			if(mPullToRefreshLayout != null)
				mPullToRefreshLayout.setRefreshComplete();
		}

		@Override
		protected void onProgress(int max, int proc) {
			onRefreshList();
			super.onProgress(max, proc);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		FilterArgument intent = new FilterArgument();
		intent.setArgument( getArguments() );
		if(!intent.hasFilterId()){
			inflater.inflate( R.menu.issues, menu );
		}
		inflater.inflate( R.menu.refresh, menu );
		menu_refresh = menu.findItem(R.id.menu_refresh);
		if(task != null && task.getStatus() == Status.RUNNING)
			menu_refresh.setEnabled(false);

		if(getActivity() instanceof SherlockFragmentActivity){
			ActionBar bar = ((SherlockFragmentActivity)getActivity()).getSupportActionBar();
			SearchView search = new SearchView(bar.getThemedContext());
			search.setIconifiedByDefault(false);
			search.setSubmitButtonEnabled(true);
			search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String s) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String s) {
					if (TextUtils.isEmpty(s)) {
						isBlockFetch = false;
						getListView().clearTextFilter();
					} else {
						isBlockFetch = true;
						getListView().setFilterText(s);
					}
					return true;
				}
			});
			menu.add(android.R.string.search_go)
					.setIcon(android.R.drawable.ic_menu_search)
					.setActionView(search)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	protected void intentFilterAction(){
		ProjectArgument intent = new ProjectArgument();
		intent.setArgument( getArguments() );
		ProjectArgument send = new ProjectArgument();
		send.setIntent( getActivity(), FilterViewActivity.class );
		send.setConnectionId(intent.getConnectionId());
		send.setProjectId(intent.getProjectId());
		startActivityForResult(send.getIntent(), ACTIVITY_FILTER);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_refresh:
			{
				this.onRefresh(true);
				return true;
			}
			case R.id.menu_issues_filter:
			{
				intentFilterAction();
				return true;
			}
			case R.id.menu_access_addnew:
			{
				ProjectArgument intent = new ProjectArgument();
				intent.setArgument( getArguments() );
				mListener.onIssueAdd(intent.getConnectionId(), intent.getProjectId());
				return true;
			}

		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case ACTIVITY_FILTER:
			if(resultCode != Activity.RESULT_OK )
				break;
			this.onRefresh(false);
			break;
		default:
			break;
		}
	}

}
