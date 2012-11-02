package jp.redmine.redmineclient;

import java.util.ArrayList;


import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.intent.ConnectionIntent;
import jp.redmine.redmineclient.intent.ProjectIntent;
import jp.redmine.redmineclient.model.ProjectModel;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ProjectListActivity extends Activity  {
	public ProjectListActivity(){
		super();
	}

	private ArrayAdapter<RedmineProject> listAdapter;
	private ProjectModel modelProject;
	private SelectDataTask task;

	@Override
	protected void onDestroy() {
		// cleanup task
		if(task != null && task.getStatus() == Status.RUNNING){
			task.cancel(true);
		}
		// cleanup models
		if(modelProject != null){
			modelProject.finalize();
			modelProject = null;
		}
		super.onDestroy();
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connectionlist);

		ConnectionIntent intent = new ConnectionIntent(getIntent());
		int id = intent.getConnectionId();
		modelProject = new ProjectModel(getApplicationContext(), id);

		ListView list = (ListView)findViewById(R.id.listConnectionList);
		listAdapter = new ArrayAdapter<RedmineProject>(
				this,android.R.layout.simple_list_item_1
				,new ArrayList<RedmineProject>());

		list.setAdapter(listAdapter);

		onReload();

		if(listAdapter.getCount() == 0){
			onRefresh();
		}

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

	protected void onReload(){
		listAdapter.notifyDataSetInvalidated();
		listAdapter.clear();
		for (RedmineProject i : modelProject.fetchAllData()){
			listAdapter.add(i);
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
		ConnectionIntent intent = new ConnectionIntent(getIntent());
		int id = intent.getConnectionId();
		task = new SelectDataTask(this);
		task.execute(id);
	}

	private class SelectDataTask extends AsyncTask<Integer, Integer, Integer> {
		private ProgressDialog dialog;
		private Context parentContext;
		public SelectDataTask(final Context tex){
			parentContext = tex;
		}
		// can use UI thread here
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(parentContext);
			dialog.setMessage(parentContext.getString(R.string.menu_settings_loading));
			dialog.show();
		}

		@Override
		protected Integer doInBackground(Integer ... params) {
			modelProject.fetchRemoteData();
			return 0;
		}
		// can use UI thread here
		@Override
		protected void onPostExecute(Integer b) {
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
