package jp.redmine.redmineclient.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.ProjectListAdapter;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.service.ExecuteMethod;
import jp.redmine.redmineclient.service.ISync;
import jp.redmine.redmineclient.service.ISyncObserver;

public class ProjectList extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor>
		,SwipeRefreshLayout.OnRefreshListener
{
	private static final String TAG = ProjectList.class.getSimpleName();
	private ProjectListAdapter adapter;
	private MenuItem menu_refresh;
	private View mFooter;
	private IssueActionInterface mListener;
	SwipeRefreshLayout mSwipeRefreshLayout;

	ISync mService = null;
	ISyncObserver mObserver = new ISyncObserver.Stub() {
		private boolean isValidKind(int kind){
			switch(ExecuteMethod.getValueOf(kind)){
				case Master:
				case Project:
					return true;
				default:
					return false;
			}
		}
		@Override
		public void onStart(int kind) throws RemoteException {
			if(!isValidKind(kind)) return;
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
			if(!isValidKind(kind)) return;
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(mFooter != null)
						mFooter.setVisibility(View.GONE);
					if(menu_refresh != null)
						menu_refresh.setEnabled(true);
					if(mSwipeRefreshLayout != null)
						mSwipeRefreshLayout.setRefreshing(false);
					if(adapter != null)
						adapter.notifyDataSetChanged();
				}
			});
		}

		@Override
		public void onError(int kind, int status) throws RemoteException {
			if(!isValidKind(kind)) return;

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
		activity.bindService(
				new Intent(ISync.class.getName()), mConnection, Context.BIND_AUTO_CREATE
		);
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

		adapter = new ProjectListAdapter(getActivity(), null, true);

		getLoaderManager().initLoader(0, getArguments(), this);
		adapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				ConnectionArgument intent = new ConnectionArgument();
				intent.setArgument(getArguments());
				int connection_id = intent.getConnectionId();
				return ProjectListAdapter.getSearchQuery(getActivity().getContentResolver(), connection_id, constraint);
			}
		});

		adapter.setOnFavoriteClickListener(new ProjectListAdapter.OnFavoriteClickListener() {
			@Override
			public void onItemClick(int position, int id, boolean b) {
				if (adapter == null)
					return;
				ProjectListAdapter.updateFavorite(getActivity().getContentResolver(), id, b);
			}
		});

		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				Object item = adapterView.getItemAtPosition(i);
				if (item == null || !(item instanceof RedmineProject))
					return false;
				RedmineProject project = (RedmineProject) item;
				mListener.onKanbanList(project.getConnectionId(), project.getId());
				return true;
			}
		});

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
		if(mService == null)
			return;
		ConnectionArgument intent = new ConnectionArgument();
		intent.setArgument(getArguments());
		try {
			mService.fetchMaster(intent.getConnectionId());
			mService.fetchProject(intent.getConnectionId());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate( R.menu.projects, menu );
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
				if(TextUtils.isEmpty(s)) {
					getListView().setFilterText(s);
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
		intent.setArgument(args);
		return ProjectListAdapter.getCursorLoader(getActivity(), intent.getConnectionId());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
		if (getListView().getAdapter() == null) {
			setListAdapter(adapter);
			adapter.notifyDataSetChanged();
			if (adapter.getCount() < 1) {
				onRefresh();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
}
