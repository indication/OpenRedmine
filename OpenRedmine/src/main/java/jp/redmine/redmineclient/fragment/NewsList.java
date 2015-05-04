package jp.redmine.redmineclient.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
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
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.adapter.NewsListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.ProjectArgument;
import jp.redmine.redmineclient.service.ExecuteMethod;
import jp.redmine.redmineclient.service.ISync;
import jp.redmine.redmineclient.service.ISyncObserver;

public class NewsList extends OrmLiteListFragment<DatabaseCacheHelper> implements SwipeRefreshLayout.OnRefreshListener {
	private static final String TAG = NewsList.class.getSimpleName();
	private NewsListAdapter adapter;
	private MenuItem menu_refresh;
	private View mFooter;
	private WebviewActionInterface mListener;

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
			ActivityHelper.toastRemoteError(getActivity(), status);
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
				case News:
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

	public NewsList(){
		super();
	}

	static public NewsList newInstance(ProjectArgument intent){
		NewsList instance = new NewsList();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = ActivityHandler.getHandler(activity, WebviewActionInterface.class);

		activity.bindService(
				new Intent(ISync.class.getName()), mConnection, Context.BIND_AUTO_CREATE
		);
	}

	@Override
	public void onDetach() {
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


		adapter = new NewsListAdapter(getHelper(), getActivity(), mListener);

		final ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());

		setListAdapter(adapter);
		adapter.setupParameter(intent.getConnectionId(), intent.getProjectId());
		adapter.notifyDataSetChanged();

		if(adapter.getCount() < 1){
			onRefresh();
		}

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

	SwipeRefreshLayout mSwipeRefreshLayout;
	@Override
	public void onListItemClick(ListView listView, View v, int position, long id) {
		super.onListItemClick(listView, v, position, id);
		Object item =  listView.getItemAtPosition(position);
		if(item == null || !(item instanceof RedmineProject))
			return;
		RedmineProject project = (RedmineProject)item;
		//mListener.onIssueList(project.getConnectionId(), project.getId());
	}

	public void onRefresh(){
		if(mService == null)
			return;
		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());
		try {
			mService.fetchNews(intent.getConnectionId(), intent.getProjectId());
		} catch (RemoteException e) {
			Log.e(TAG, "onRefresh", e);
		}
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate( R.menu.refresh, menu );
		menu_refresh = menu.findItem(R.id.menu_refresh);

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
				return false;
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

}
