package jp.redmine.redmineclient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.store.RedmineConnectionModel;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.intent.ConnectionIntent;
import jp.redmine.redmineclient.intent.ProjectIntent;
import jp.redmine.redmineclient.task.SelectProjectTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ProjectListActivity extends DbBaseActivity  {
	public ProjectListActivity(){
		super();
	}

	private ArrayAdapter<RedmineProject> listAdapter;
	private RedmineProjectModel modelProject;
	private SelectDataTask task;

	@Override
	protected void onDestroy() {
		cancelTask();
		super.onDestroy();
	}
	protected void cancelTask(){
		// cleanup task
		if(task != null && task.getStatus() == Status.RUNNING){
			task.cancel(true);
		}
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connectionlist);

		modelProject = new RedmineProjectModel(getHelperCache());

		ListView list = (ListView)findViewById(R.id.listConnectionList);
		listAdapter = new ArrayAdapter<RedmineProject>(
				this,android.R.layout.simple_list_item_1
				,new ArrayList<RedmineProject>());

		list.setAdapter(listAdapter);


		//リスト項目がクリックされた時の処理
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				ListView listView = (ListView) parent;
				RedmineProject item = (RedmineProject) listView.getItemAtPosition(position);
				onItemSelect(item);
			}

		});

		/*
		//リスト項目が長押しされた時の処理
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				ListView listView = (ListView) parent;
				RedmineConnection item = (RedmineConnection) listView.getItemAtPosition(position);
				Bundle bundle = new Bundle();
				bundle.putInt(DIALOG_PARAM_ID, item.Id());
				bundle.putString(DIALOG_PARAM_NAME, item.Name());
				showDialog(DIALOG_ITEM_ACTION, bundle);
				return false;
			}
		});
		*/
	}

	@Override
	protected void onStart() {
		super.onStart();
		onReload();
		if(listAdapter.getCount() == 0){
			onRefresh();
		}
	}

	protected void onReload(){
		listAdapter.notifyDataSetInvalidated();
		listAdapter.clear();
		ConnectionIntent intent = new ConnectionIntent(getIntent());
		int id = intent.getConnectionId();
		try {
			for (RedmineProject i : modelProject.fetchAll(id)){
				listAdapter.add(i);
			}
		} catch (SQLException e) {
			Log.e("ProjectListActivity","onReload",e);
		}
		listAdapter.notifyDataSetChanged();
	}

	protected void onItemSelect(RedmineProject item) {

		ProjectIntent intent = new ProjectIntent( getApplicationContext(), IssueListActivity.class );
		intent.setConnectionId(item.getConnectionId());
		intent.setProjectId(item.getId());
		startActivity( intent.getIntent() );
	}
	protected void onRefresh(){
		if(task != null && task.getStatus() == Status.RUNNING){
			return;
		}
		task = new SelectDataTask(this);
		task.execute();
	}

	private class SelectDataTask extends SelectProjectTask {
		private ProgressDialog dialog;
		private Context parentContext;
		public SelectDataTask(final Context tex){
			parentContext = tex;
			ConnectionIntent intent = new ConnectionIntent(getIntent());
			int id = intent.getConnectionId();
			RedmineConnectionModel model = new RedmineConnectionModel(getHelperStore());
			try {
				connection = model.fetchById(id);
			} catch (SQLException e) {
				Log.e("ProjectListActivity","SelectDataTask",e);
			}
		}
		// can use UI thread here
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(parentContext);
			dialog.setMessage(parentContext.getString(R.string.menu_settings_loading));
			dialog.show();
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(List<RedmineProject> b) {
			onReload();
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu( menu );

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.projects, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_projects_refresh:
			{
				this.onRefresh();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
