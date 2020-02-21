package jp.redmine.redmineclient.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.ListFragmentSwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.FilterViewActivity;
import jp.redmine.redmineclient.activity.WebViewActivity;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.IssueListAdapter;
import jp.redmine.redmineclient.adapter.IssueListAdapterAll;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.TypeConverter;
import jp.redmine.redmineclient.fragment.form.IssueFilterHeaderForm;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.param.FilterArgument;
import jp.redmine.redmineclient.param.ProjectArgument;
import jp.redmine.redmineclient.param.WebArgument;
import jp.redmine.redmineclient.task.SelectIssueTaskAll;
import jp.redmine.redmineclient.task.SelectProjectEnumerationTask;

public class IssueListAll extends OrmLiteListFragment<DatabaseCacheHelper> implements SwipeRefreshLayout.OnRefreshListener {
	private static final String TAG = IssueListAll.class.getSimpleName();
	private static final int ACTIVITY_FILTER = 2001;
	private IssueListAdapterAll adapter;
	private SelectDataTask task;
	private MenuItem menu_refresh;
	private MenuItem menu_add;
	private View mFooter;
	private View mHeader;
	private long lastPos = -1;
	private boolean isBlockFetch = false;

	private IssueActionInterface mListener;
	public IssueListAll(){
		super();
	}

	static public IssueListAll newInstance(ConnectionArgument arg){
		IssueListAll fragment = new IssueListAll();
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

		mListener = ActivityHandler.getHandler(getActivity(), IssueActionInterface.class);
		getListView().addFooterView(mFooter);
		getListView().addHeaderView(mHeader);

		getListView().setFastScrollEnabled(true);
		getListView().setTextFilterEnabled(true);

		adapter = new IssueListAdapterAll(getHelper(), getActivity());
		FilterArgument intent = new FilterArgument();
		intent.setArgument( getArguments() );
		if(intent.hasFilterId()){
			adapter.setupParameter(intent.getFilterId());
		} else {
			adapter.setupParameter(intent.getConnectionId(), 10);
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
	SwipeRefreshLayout mSwipeRefreshLayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFooter = inflater.inflate(R.layout.listview_footer,null);
		mFooter.setVisibility(View.GONE);
		mHeader = inflater.inflate(R.layout.listheader_filter,null);
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
		onRefresh();
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		super.onListItemClick(parent, v, position, id);
		if(v == mHeader){
			if(adapter != null && adapter.getParameter() != null && adapter.getParameter().isCurrent())
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
		RedmineConnection connection = ConnectionModel.getItem(getActivity(), intent.getConnectionId());
		if(intent.hasFilterId())
			task = new SelectDataTask(helper,connection,null,intent.getFilterId());
		else
			task = new SelectDataTask(helper,connection,1);

		task.execute(0,10,isFlush ? 1 : 0);
		/*
		if(isFlush && !intent.hasFilterId()){
			RedmineProject project = null;
			RedmineProjectModel mProject = new RedmineProjectModel(helper);
			try {
				project = mProject.fetchById(intent.getProjectId());
				if(menu_add != null)
					menu_add.setEnabled(project.getStatus().isUpdateable());
			} catch (SQLException e) {
				Log.e(TAG,"SelectDataTask",e);
			}
			SelectProjectEnumerationTask enumtask = new SelectProjectEnumerationTask(helper,connection,project);
			enumtask.execute();
		}
		*/
	}
	protected void onRefreshList(){
		if(adapter == null)
			return;
		adapter.notifyDataSetChanged();
		IssueFilterHeaderForm form = new IssueFilterHeaderForm(mHeader);
		RedmineFilter filter = adapter.getParameter();
		form.setValue(filter);
		if (filter != null && !TextUtils.isEmpty(filter.getName())){
			getActivity().setTitle(filter.getName());
		}

	}

	@Override
	public void onRefresh() {
		onRefresh(true);
	}

	private class SelectDataTask extends SelectIssueTaskAll {
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
			if(mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
				mSwipeRefreshLayout.setRefreshing(true);
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(Void b) {
			mFooter.setVisibility(View.GONE);
			onRefreshList();
			if(menu_refresh != null)
				menu_refresh.setEnabled(true);
			if(mSwipeRefreshLayout != null)
				mSwipeRefreshLayout.setRefreshing(false);
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
		menu_add = menu.findItem(R.id.menu_access_addnew);
		if(task != null && task.getStatus() == Status.RUNNING)
			menu_refresh.setEnabled(false);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			setupSearchBar(menu);
		inflater.inflate( R.menu.web, menu );
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
				Integer issue_id = TypeConverter.parseInteger(s);
				if (issue_id != null){
					ConnectionArgument intent = new ConnectionArgument();
					intent.setArgument(getArguments());
					mListener.onIssueSelected(intent.getConnectionId(), issue_id);
					return true;
				} else {
					return onQueryTextChange(s);
				}
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
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
		;
	}

	protected void intentFilterAction(){
		ConnectionArgument intent = new ConnectionArgument();
		//intent.setArgument( getArguments() );
		ConnectionArgument send = new ConnectionArgument();
		send.setIntent( getActivity(), FilterViewActivity.class );
		send.setConnectionId(intent.getConnectionId());
		//send.setProjectId(intent.getProjectId());
		startActivityForResult(send.getIntent(), ACTIVITY_FILTER);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return false;
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
