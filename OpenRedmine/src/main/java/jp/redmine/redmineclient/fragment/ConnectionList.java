package jp.redmine.redmineclient.fragment;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.AboutActivity;
import jp.redmine.redmineclient.activity.CommonPreferenceActivity;
import jp.redmine.redmineclient.activity.handler.ConnectionActionInterface;
import jp.redmine.redmineclient.adapter.ConnectionListAdapter;
import jp.redmine.redmineclient.db.store.DatabaseHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;

public class ConnectionList extends ListFragment {
	private DatabaseHelper helperStore;
	private ConnectionListAdapter adapter;
	private View mFooter;
	private ConnectionActionInterface mListener;
	public ConnectionList(){
		super();
	}

	static public ConnectionList newInstance(){
		return new ConnectionList();
	}

	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);
		mListener = ActivityHandler.getHandler(activity, ConnectionActionInterface.class);

	}

	@Override
	public void onDestroyView() {
		setListAdapter(null);
		super.onDestroyView();
		if(helperStore != null){
			helperStore.close();
			helperStore = null;
		}
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().addFooterView(mFooter);
		getListView().setFastScrollEnabled(true);

		helperStore = new DatabaseHelper(getActivity());

		getListView().setOnItemLongClickListener((parent, v, position, id) -> {
			ListView listView = (ListView) parent;
			RedmineConnection item = (RedmineConnection) listView.getItemAtPosition(position);
			mListener.onConnectionEdit(item.getId());
			return true;
		});

		mFooter.setOnClickListener(v -> mListener.onConnectionAdd());

		adapter = new ConnectionListAdapter(helperStore, getActivity());
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFooter = inflater.inflate(R.layout.listview_add,null);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		super.onListItemClick(parent, v, position, id);
		RedmineConnection item = (RedmineConnection) parent.getItemAtPosition(position);
		mListener.onConnectionSelected(item.getId());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.connection, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_access_addnew:
			{
				mListener.onConnectionAdd();
				return true;
			}
			case R.id.menu_access_removecache:
			{
				ClearCacheAndRestart();
				//@todo show dialog
				return true;
			}
			case R.id.menu_settings:
			{
				Intent intent = new Intent( getActivity().getApplicationContext(), CommonPreferenceActivity.class );
				startActivity( intent );

				return true;
			}
			case R.id.menu_about:
			{
				Intent intent = new Intent( getActivity().getApplicationContext(), AboutActivity.class );
				startActivity( intent );

				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}


	private void ClearCacheAndRestart(){
		FragmentActivity context = getActivity();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ActivityManager am = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
			am.clearApplicationUserData();
		} else {
			Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ BuildConfig.APPLICATION_ID));
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		// Force close app to close database completely
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
