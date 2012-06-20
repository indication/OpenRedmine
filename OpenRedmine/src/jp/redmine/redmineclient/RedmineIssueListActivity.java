package jp.redmine.redmineclient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.RedmineConnectionModel;
import jp.redmine.redmineclient.db.RedmineIssueModel;
import jp.redmine.redmineclient.db.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.external.DataCreationHandler;
import jp.redmine.redmineclient.external.Fetcher;
import jp.redmine.redmineclient.external.ParserIssue;
import jp.redmine.redmineclient.external.RemoteUrlIssue;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RedmineIssueListActivity extends Activity  {
	public static final String INTENT_INT_CONNECTION_ID = "CONNECTIONID";
	public static final String INTENT_INT_PROJECT_ID = "PROJECTID";

	private ArrayAdapter<RedmineIssue> listAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issuelist);

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
		Intent intent = getIntent();
		int connectionid = intent.getIntExtra(INTENT_INT_CONNECTION_ID, -1);
		int projectid = intent.getIntExtra(INTENT_INT_PROJECT_ID, -1);
		final RedmineIssueModel model = new RedmineIssueModel(getBaseContext());
		List<RedmineIssue> issues = new ArrayList<RedmineIssue>();
		try {
			issues = model.fetchAllById(connectionid, projectid);
		} catch (SQLException e) {
			Log.e("SelectDataTask","doInBackground",e);
		} catch (Throwable e) {
			Log.e("SelectDataTask","doInBackground",e);
		}
		listAdapter.notifyDataSetInvalidated();
		listAdapter.clear();
		for (RedmineIssue i : issues){
			listAdapter.add(i);
		}
		listAdapter.notifyDataSetChanged();
	}

	protected void onRefresh(){
		Intent intent = getIntent();
		int id = intent.getIntExtra(INTENT_INT_CONNECTION_ID, -1);
		int projectid = intent.getIntExtra(INTENT_INT_PROJECT_ID, -1);
		(new SelectDataTask(this,0,0)).execute(id,projectid);
	}

	private class SelectDataTask extends AsyncTask<Integer, Integer, int[]> {
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
		}

		@Override
		protected int[] doInBackground(Integer ... params) {
			final RedmineConnectionModel connection =
				new RedmineConnectionModel(getBaseContext());
			final RedmineProjectModel project =
				new RedmineProjectModel(getBaseContext());
			final int id = params[0];
			final int projectid = params[1];
			int count = 0;

			RedmineConnection info = null;
			RedmineProject proj = null;
			Log.d("SelectDataTask","ParserProject Start");
			try {
				info = connection.fetchById(id);
				proj = project.fetchById(id, projectid);
			} catch (SQLException e) {
				Log.e("SelectDataTask","ParserProject",e);
			}

			if(info != null) {
				count = fetchIssue(info,proj);
			}
			return new int[]{id,projectid,count};
		}
		// can use UI thread here
		@Override
		protected void onPostExecute(int[] params) {
			if(params == null || params[2] == 0){

			} else if (params[2] == MAXLOAD){
				(new SelectDataTask(parentContext,lastloaded + MAXLOAD,limitloaded))
				.execute(params[0],params[1]);
			}
			onReload();
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		//@todo clean up
		protected int fetchIssue(RedmineConnection info,RedmineProject project){
			final RedmineIssueModel model =
				new RedmineIssueModel(getBaseContext());
			RemoteUrlIssue url = new RemoteUrlIssue();
			Fetcher<RedmineProject> fetch = new Fetcher<RedmineProject>();
			ParserIssue parser = new ParserIssue();
			parser.registerDataCreation(new DataCreationHandler<RedmineProject,RedmineIssue>() {
				public void onData(RedmineProject proj,RedmineIssue data) {
					Log.d("ParserIssue","OnData Called");
					try {
						model.refreshItem(proj,data);
					} catch (SQLException e) {
						Log.e("ParserIssue","onData",e);
					}
				}
			});

			url.filterProject(project.getProjectId().toString());
			url.filterOffset(lastloaded);
			url.filterLimit(MAXLOAD);
			Log.d("SelectDataTask","ParserProject Start");
			try {
				fetch.setRemoteurl(url);
				fetch.setParser(parser);
				fetch.fetchData(info,project);

			} catch (XmlPullParserException e) {
				Log.e("SelectDataTask","fetchIssue",e);
			} catch (IOException e) {
				Log.e("SelectDataTask","fetchIssue",e);
			}
			return parser.getCount();
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
