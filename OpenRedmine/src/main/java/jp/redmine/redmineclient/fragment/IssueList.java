package jp.redmine.redmineclient.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.widget.ListFragmentSwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.IssueListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.fragment.form.IssueFilterHeaderForm;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.param.FilterArgument;
import jp.redmine.redmineclient.param.ProjectArgument;
import jp.redmine.redmineclient.service.ExecuteMethod;
import jp.redmine.redmineclient.service.ISync;
import jp.redmine.redmineclient.service.ISyncObserver;

public class IssueList extends OrmLiteListFragment<DatabaseCacheHelper> implements SwipeRefreshLayout.OnRefreshListener {
	private static final String TAG = IssueList.class.getSimpleName();
	private static final int ACTIVITY_FILTER = 2001;
	private IssueListAdapter adapter;
	private MenuItem menu_refresh;
	private MenuItem menu_add;
	private View mFooter;
	private View mHeader;
	private long lastPos = -1;
	private boolean isBlockFetch = false;

	private IssueActionInterface mListener;

	ISync mService = null;
	ISyncObserver mObserver = new ISyncObserver.Stub() {
		@Override
		public void onStart(int kind) throws RemoteException {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(mFooter != null)
						mFooter.setVisibility(View.VISIBLE);
					if(menu_refresh != null)
						menu_refresh.setEnabled(false);
					if(mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
						mSwipeRefreshLayout.setRefreshing(true);
				}
			});
		}

		@Override
		public void onStop(int kind) throws RemoteException {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(mFooter != null)
						mFooter.setVisibility(View.GONE);
					if(menu_refresh != null)
						menu_refresh.setEnabled(true);
					if(mSwipeRefreshLayout != null)
						mSwipeRefreshLayout.setRefreshing(false);
				}
			});
		}

		@Override
		public void onError(int kind, int status) throws RemoteException {
			//TODO
		}

		@Override
		public void onChanged(int kind) throws RemoteException {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(adapter != null)
						adapter.notifyDataSetChanged();
				}
			});
		}

		@Override
		public boolean isNotify(int kind) throws RemoteException {
			switch(ExecuteMethod.getValueOf(kind)){
				case Issues:
					return true;
				default:
					return false;
			}
		}
	};

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ISync.Stub.asInterface(service);
			if (mService != null) {
				try {
					mService.setObserver(mObserver);
				} catch (RemoteException e) {
					Log.e(TAG, "onServiceConnected", e);
				}
			}
		}

		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = ActivityHandler.getHandler(activity, IssueActionInterface.class);
		activity.bindService(
				new Intent(ISync.class.getName()), mConnection, Context.BIND_AUTO_CREATE
		);
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
		setListAdapter(null);
		super.onDestroyView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().addFooterView(mFooter);

		getListView().setFastScrollEnabled(true);
		getListView().setTextFilterEnabled(true);

		Toolbar bar = (Toolbar)(getActivity().findViewById(R.id.toolbar_actionbar));
		if(bar != null) {
			bar.addView(mHeader, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			bar.setTitle("");
		} else {
			getListView().addHeaderView(mHeader);
		}

		adapter = new IssueListAdapter(getHelper(), getActivity());
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
					if(isFetching())
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

	protected boolean isFetching(){
		return mFooter != null && mFooter.getVisibility() == View.VISIBLE;
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
	public void onDetach() {
		Toolbar bar = (Toolbar)(getActivity().findViewById(R.id.toolbar_actionbar));
		if(bar != null)
			bar.removeView(mHeader);
		if(mService != null){
			try {
				mService.removeObserver(mObserver);
			} catch (RemoteException e) {
				Log.e(TAG, "onServiceDisconnected", e);
			}
		}
		getActivity().unbindService(mConnection);
		super.onDetach();
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
		if(isFetching() || mService == null){
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
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean isFetchAll = sp.getBoolean("issue_get_all", false);
		int offset = isFlush ? -1 : 0;
		try {
			if(intent.hasFilterId())
				mService.fetchIssuesByFilter(intent.getConnectionId(), intent.getFilterId(), offset, isFetchAll);
			else
				mService.fetchIssuesByProject(intent.getConnectionId(), intent.getProjectId(), offset, isFetchAll);
		} catch (RemoteException e) {
			Log.e(TAG, "onRefresh", e);
		}

		if(isFlush && !intent.hasFilterId()){
			RedmineProject project = null;
			RedmineProjectModel mProject = new RedmineProjectModel(getHelper());
			try {
				project = mProject.fetchById(intent.getProjectId());
				if(menu_add != null)
					menu_add.setEnabled(project.getStatus().isUpdateable());
			} catch (SQLException e) {
				Log.e(TAG,"SelectDataTask",e);
			}
		}
	}
	protected void onRefreshList(){
		if(adapter == null)
			return;
		adapter.notifyDataSetChanged();
		IssueFilterHeaderForm form = new IssueFilterHeaderForm(mHeader);
		form.setValue(adapter.getParameter());

	}

	@Override
	public void onRefresh() {
		onRefresh(true);
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
		if(isFetching())
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
