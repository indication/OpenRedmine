package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.widget.ListFragmentSwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import jp.redmine.redmineclient.adapter.WikiListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineWiki;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.ProjectArgument;
import jp.redmine.redmineclient.service.ExecuteMethod;
import jp.redmine.redmineclient.service.ISync;
import jp.redmine.redmineclient.service.ISyncObserver;

public class WikiList extends OrmLiteListFragment<DatabaseCacheHelper> implements SwipeRefreshLayout.OnRefreshListener {
	private static final String TAG = WikiList.class.getSimpleName();
	private WikiListAdapter adapter;
	private View mFooter;
	private MenuItem menu_refresh;
	SwipeRefreshLayout mSwipeRefreshLayout;

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
				case Wiki:
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
				if(adapter.getCount() < 1) {
					onRefresh();
				}
			}
		}

		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
	};

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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = ActivityHandler.getHandler(activity, WebviewActionInterface.class);
		activity.bindService(
				new Intent(ISync.class.getName()), mConnection, Context.BIND_AUTO_CREATE
		);
	}
	public WikiList(){
		super();
	}

	static public WikiList newInstance(ProjectArgument arg){
		WikiList fragment = new WikiList();
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

		adapter = new WikiListAdapter(getHelper(), getActivity());
		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());
		adapter.setupParameter(intent.getConnectionId(),intent.getProjectId());
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();

        if(adapter.getCount() == 0)
            onRefresh();

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
		if(listitem == null || !RedmineWiki.class.isInstance(listitem)  )
		{
			return;
		}
		RedmineWiki item = (RedmineWiki) listitem;
		mListener.wiki(item.getConnectionId(), item.getProject().getId(), item.getTitle());
	}


	protected boolean isFetching(){
		return mFooter != null && mFooter.getVisibility() == View.VISIBLE;
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
	public void onRefresh(){
		if(mService == null) {
			return;
		}
		if(isFetching()){
			return;
		}
		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());
		try {
			mService.fetchWikiByProject(intent.getConnectionId(), intent.getProjectId());
		} catch (RemoteException e) {
			Log.e(TAG, "onRefresh", e);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh, menu);
		menu_refresh = menu.findItem(R.id.menu_refresh);
		if(isFetching())
			menu_refresh.setEnabled(false);
		super.onCreateOptionsMenu(menu, inflater);
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
