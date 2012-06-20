package jp.redmine.redmineclient;

import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.adapter.RedmineIssueListAdapter;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.model.IssueModel;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class IssueListActivity extends Activity  {
	public static final String INTENT_INT_CONNECTION_ID = "CONNECTIONID";
	public static final String INTENT_INT_PROJECT_ID = "PROJECTID";

	private IssueModel modelIssue;
	private ArrayAdapter<RedmineIssue> listAdapter;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(modelIssue != null){
			modelIssue.finalize();
			modelIssue = null;
		}
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issuelist);

		Intent intent = getIntent();
		int connectionid = intent.getIntExtra(INTENT_INT_CONNECTION_ID, -1);
		int projectid = intent.getIntExtra(INTENT_INT_PROJECT_ID, -1);
		modelIssue = new IssueModel(getApplicationContext(), connectionid,projectid);

		ListView list = (ListView)findViewById(R.id.listConnectionList);
		listAdapter = new RedmineIssueListAdapter(
				this,R.layout.issueitem
				,new ArrayList<RedmineIssue>());

		list.setAdapter(listAdapter);

		onReload();

		if(listAdapter.getCount() == 0){
			onRefresh();
		}

		//リスト項目がクリックされた時の処理
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				//ListView listView = (ListView) parent;
				//RedmineProject item = (RedmineProject) listView.getItemAtPosition(position);
				//onItemSelect(item.Id());
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
		List<RedmineIssue> issues = modelIssue.fetchAllData(0,0);
		listAdapter.notifyDataSetInvalidated();
		listAdapter.clear();
		for (RedmineIssue i : issues){
			listAdapter.add(i);
		}
		listAdapter.notifyDataSetChanged();
	}

	protected void onRefresh(){
		(new SelectDataTask(this,0,0)).execute(0);
	}

	private class SelectDataTask extends AsyncTask<Integer, Integer, Integer> {
		private ProgressDialog dialog;
		private Context parentContext;
		private final int MAXLOAD = 50;
		private int lastloaded = 0;
		private int limitloaded = 0;
		public SelectDataTask(final Context tex,int loaded,int limit){
			lastloaded = loaded;
			limitloaded = limit;
			parentContext = tex;
		}
		// can use UI thread here
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(parentContext);
			dialog.setMessage(parentContext.getString(R.string.menu_settings_loading));
			dialog.show();
			dialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					limitloaded = -1;
				}
			});
		}

		@Override
		protected Integer doInBackground(Integer ... params) {
			int count = modelIssue.fetchRemoteData(lastloaded,MAXLOAD);
			return count;
		}
		// can use UI thread here
		@Override
		protected void onPostExecute(Integer params) {
			if(lastloaded == 0){
				onReload();
			}
			if (limitloaded != 0 && (lastloaded + MAXLOAD) > limitloaded){
			} else if (params == MAXLOAD){
				(new SelectDataTask(parentContext,lastloaded + MAXLOAD,limitloaded))
				.execute(0);
			}
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
