package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.io.File;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.AboutActivity;
import jp.redmine.redmineclient.activity.CommonPreferenceActivity;
import jp.redmine.redmineclient.activity.handler.ConnectionActionInterface;
import jp.redmine.redmineclient.adapter.ConnectionListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.store.DatabaseHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;

public class ConnectionList extends SherlockListFragment {
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
	public void onAttach(Activity activity) {
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

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				ListView listView = (ListView) parent;
				RedmineConnection item = (RedmineConnection) listView.getItemAtPosition(position);
				mListener.onConnectionEdit(item.getId());
				return true;
			}
		});

		mFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onConnectionAdd();
			}
		});

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
				String path = DatabaseCacheHelper.getDatabasePath(getActivity().getApplicationContext());
				File file = new File(path);
				file.delete();
				Log.d("Cache Deleted",path);
				getActivity().finish();
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

}
