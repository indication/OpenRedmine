package jp.redmine.redmineclient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.intent.ConnectionIntent;
import jp.redmine.redmineclient.model.ConnectionModel;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ConnectionListActivity extends Activity {
	public ConnectionListActivity(){
		super();
	}
	static final int DIALOG_ITEM_ACTION = 0;
	static final int DIALOG_CONFIRM_DELETE = 1;
	static final String DIALOG_PARAM_ID = "ID";
	static final String DIALOG_PARAM_NAME = "NAME";
	private NotificationManager notifManager;
	private View mFooter;
	private ArrayAdapter<RedmineConnection> listAdapter;

	private ConnectionModel modelConnection;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(modelConnection != null){
			modelConnection.finalize();
			modelConnection = null;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connectionlist);

		notifManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		modelConnection = new ConnectionModel(getApplicationContext());


		ListView list = (ListView)findViewById(R.id.listConnectionList);
		listAdapter = new ArrayAdapter<RedmineConnection>(
				this,android.R.layout.simple_list_item_1
				,new ArrayList<RedmineConnection>());

		list.addFooterView(getAddView());
		list.setAdapter(listAdapter);

		//リスト項目がクリックされた時の処理
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				ListView listView = (ListView) parent;
				RedmineConnection item = (RedmineConnection) listView.getItemAtPosition(position);
				onItemSelect(item.getId());
			}
		});

		//リスト項目が長押しされた時の処理
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				ListView listView = (ListView) parent;
				RedmineConnection item = (RedmineConnection) listView.getItemAtPosition(position);
				showDialogItemSelected(item.getId(),item.getName());
				return true;
			}
		});
	}

	protected View getAddView(){
		if (mFooter == null) {
			mFooter = getLayoutInflater()
				.inflate(R.layout.listview_add,null);
			mFooter.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onItemNew();
				}
			});

		}
		return mFooter;

	}

	@Override
	protected void onStart() {
		super.onStart();

		onReload();
	}

	protected void onItemSelect(int id){
		ConnectionIntent intent = new ConnectionIntent( getApplicationContext(), ProjectListActivity.class );
		intent.setConnectionId(id);
		startActivity( intent.getIntent() );
	}
	@Override
	protected void onResume() {
		super.onResume();
		onReload();
	}

	protected void onReload(){
		(new SelectDataTask()).execute("");
	}

	protected void onItemNew(){
		onItemEdit(-1);
	}
	protected void onItemEdit(int itemid){
		ConnectionIntent intent = new ConnectionIntent( getApplicationContext(), ConnectionActivity.class );
		intent.setConnectionId(itemid);
		startActivity( intent.getIntent() );
	}

	protected void onItemDelete(int itemid){
		modelConnection.deleteItem(itemid);
		onReload();
	}

	protected void notifyBibrate(){
		Notification notif = new Notification();
		notif.vibrate = new long[]{50,100};
		notifManager.notify(R.string.app_name, notif);
	}

	protected void showDialogDeleteItem(final int id,String name){
		notifyBibrate();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(String.format(
				this.getString(R.string.menu_setting_list_menu_title)
				, name));
		builder.setMessage(getString(R.string.menu_confirm_delete_msg))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.menu_config_delete_yes)
					, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int index) {
					onItemDelete(id);
				}
			})
			.setNegativeButton(getString(R.string.menu_config_delete_no)
					, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int index) {
				}
			});
		Dialog dialog = builder.create();
		dialog.show();
	}

	protected void showDialogItemSelected(final int id,final String name){
		notifyBibrate();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(String.format(
				this.getString(R.string.menu_setting_list_menu_title)
				, name));
		final CharSequence[] items = {
			 this.getString(R.string.menu_setting_list_menu_edit)
			,this.getString(R.string.menu_setting_list_menu_delete)
			};
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch(item){
				case 1:
					showDialogDeleteItem(id,name);
					break;
				case 0:
					onItemEdit(id);
					break;
				default:
				}
			}
		});
		Dialog dialog = builder.create();
		dialog.show();
	}



	private class SelectDataTask extends AsyncTask<String, Integer, List<RedmineConnection>> {
		@Override
		protected List<RedmineConnection> doInBackground(String ... params) {
			return modelConnection.fetchAllData();
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(List<RedmineConnection> b) {
			listAdapter.notifyDataSetInvalidated();
			listAdapter.clear();
			for (RedmineConnection i : b){
				listAdapter.add(i);
			}
			listAdapter.notifyDataSetChanged();

		}

	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu( menu );

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.connection, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_access_addnew:
			{
				onItemNew();
				return true;
			}
			case R.id.menu_access_removecache:
			{
				String path = DatabaseCacheHelper.getDatabasePath(getApplicationContext());
				File file = new File(path);
				file.delete();
				Log.d("Cache Deleted",path);
				this.finish();
				//@todo show dialog
				return true;
			}
			case R.id.menu_settings:
			{
				Intent intent = new Intent( getApplicationContext(), CommonPreferenceActivity.class );
				startActivity( intent );

				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}