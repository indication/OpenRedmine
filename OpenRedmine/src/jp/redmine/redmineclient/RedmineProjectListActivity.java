package jp.redmine.redmineclient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.RedmineConnectionModel;
import jp.redmine.redmineclient.db.RedmineProjectModel;
import jp.redmine.redmineclient.db.RedmineStatusModel;
import jp.redmine.redmineclient.db.RedmineTrackerModel;
import jp.redmine.redmineclient.db.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.external.DataCreationHandler;
import jp.redmine.redmineclient.external.Fetcher;
import jp.redmine.redmineclient.external.ParserProject;
import jp.redmine.redmineclient.external.ParserStatus;
import jp.redmine.redmineclient.external.ParserTracker;
import jp.redmine.redmineclient.external.ParserUser;
import jp.redmine.redmineclient.external.RemoteUrlProjects;
import jp.redmine.redmineclient.external.RemoteUrlStatus;
import jp.redmine.redmineclient.external.RemoteUrlTrackers;
import jp.redmine.redmineclient.external.RemoteUrlUsers;
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

public class RedmineProjectListActivity extends Activity  {
	public static final String INTENT_INT_CONNECTION_ID = "CONNECTIONID";

	private ArrayAdapter<RedmineProject> listAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connectionlist);

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
		Intent intent = getIntent();
		int id = intent.getIntExtra(INTENT_INT_CONNECTION_ID, -1);
		final RedmineProjectModel model = new RedmineProjectModel(getBaseContext());
		List<RedmineProject> projects = new ArrayList<RedmineProject>();
		try {
			projects = model.fetchAll(id);
		} catch (SQLException e) {
			Log.e("SelectDataTask","doInBackground",e);
		} catch (Throwable e) {
			Log.e("SelectDataTask","doInBackground",e);
		}
		listAdapter.notifyDataSetInvalidated();
		listAdapter.clear();
		for (RedmineProject i : projects){
			listAdapter.add(i);
		}
		listAdapter.notifyDataSetChanged();
	}

	protected void onItemSelect(RedmineProject item) {

		Intent intent = new Intent( getApplicationContext(), RedmineIssueListActivity.class );
		intent.putExtra(RedmineIssueListActivity.INTENT_INT_CONNECTION_ID, item.getConnectionId());
		intent.putExtra(RedmineIssueListActivity.INTENT_INT_PROJECT_ID, item.getProjectId());
		startActivity( intent );
	}
	protected void onRefresh(){
		Intent intent = getIntent();
		int id = intent.getIntExtra(INTENT_INT_CONNECTION_ID, -1);
		(new SelectDataTask(this)).execute(id);
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
			final RedmineConnectionModel connection =
				new RedmineConnectionModel(getBaseContext());
			final int id = params[0];

			RedmineConnection info = null;
			Log.d("SelectDataTask","ParserProject Start");
			try {
				info = connection.fetchById(id);
			} catch (SQLException e) {
				Log.e("SelectDataTask","ParserProject",e);
			}

			if(info != null) {
				fetchProject(info);
				fetchUsers(info);
				fetchTrackers(info);
				fetchStatus(info);
			}
			return id;
		}
		// can use UI thread here
		@Override
		protected void onPostExecute(Integer b) {
			onReload();
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		//@todo clean up
		protected void fetchProject(RedmineConnection info){
			final RedmineProjectModel model =
				new RedmineProjectModel(getBaseContext());
			RemoteUrlProjects url = new RemoteUrlProjects();
			Fetcher<RedmineConnection> fetch = new Fetcher<RedmineConnection>();
			ParserProject parser = new ParserProject();
			parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineProject>() {
				public void onData(RedmineConnection con,RedmineProject data) {
					Log.d("ParserProject","OnData Called");
					try {
						model.refreshItem(con,data);
					} catch (SQLException e) {
						Log.e("ParserProject","onData",e);
					}
				}
			});

			Log.d("SelectDataTask","ParserProject Start");
			try {
				fetch.setRemoteurl(url);
				fetch.setParser(parser);
				fetch.fetchData(info,info);

			} catch (XmlPullParserException e) {
				Log.e("SelectDataTask","fetchProject",e);
			} catch (IOException e) {
				Log.e("SelectDataTask","fetchProject",e);
			}
		}

		protected void fetchUsers(RedmineConnection info){
			final RedmineUserModel model =
				new RedmineUserModel(getBaseContext());
			RemoteUrlUsers url = new RemoteUrlUsers();
			Fetcher<RedmineConnection> fetch = new Fetcher<RedmineConnection>();
			ParserUser parser = new ParserUser();
			parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineUser>() {
				public void onData(RedmineConnection con,RedmineUser data) {
					Log.d("ParserUser","OnData Called");
					try {
						model.refreshItem(con,data);
					} catch (SQLException e) {
						Log.e("ParserUser","onData",e);
					}
				}
			});

			Log.d("SelectDataTask","ParserUser Start");
			try {
				fetch.setRemoteurl(url);
				fetch.setParser(parser);
				fetch.fetchData(info,info);

			} catch (XmlPullParserException e) {
				Log.e("SelectDataTask","fetchUsers",e);
			} catch (IOException e) {
				Log.e("SelectDataTask","fetchUsers",e);
			}
		}
		protected void fetchTrackers(RedmineConnection info){
			final RedmineTrackerModel model =
				new RedmineTrackerModel(getBaseContext());
			RemoteUrlTrackers url = new RemoteUrlTrackers();
			Fetcher<RedmineConnection> fetch = new Fetcher<RedmineConnection>();
			ParserTracker parser = new ParserTracker();
			parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineTracker>() {
				public void onData(RedmineConnection con,RedmineTracker data) {
					Log.d("ParserTracker","OnData Called");
					try {
						model.refreshItem(con,data);
					} catch (SQLException e) {
						Log.e("ParserTracker","onData",e);
					}
				}
			});

			Log.d("SelectDataTask","ParserTracker Start");
			try {
				fetch.setRemoteurl(url);
				fetch.setParser(parser);
				fetch.fetchData(info,info);

			} catch (XmlPullParserException e) {
				Log.e("SelectDataTask","fetchTrackers",e);
			} catch (IOException e) {
				Log.e("SelectDataTask","fetchTrackers",e);
			}
		}
		protected void fetchStatus(RedmineConnection info){
			final RedmineStatusModel model =
				new RedmineStatusModel(getBaseContext());
			RemoteUrlStatus url = new RemoteUrlStatus();
			Fetcher<RedmineConnection> fetch = new Fetcher<RedmineConnection>();
			ParserStatus parser = new ParserStatus();
			parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineStatus>() {
				public void onData(RedmineConnection con,RedmineStatus data) {
					Log.d("ParserStatus","OnData Called");
					try {
						model.refreshItem(con,data);
					} catch (SQLException e) {
						Log.e("ParserStatus","onData",e);
					}
				}
			});

			Log.d("SelectDataTask","ParserStatus Start");
			try {
				fetch.setRemoteurl(url);
				fetch.setParser(parser);
				fetch.fetchData(info,info);

			} catch (XmlPullParserException e) {
				Log.e("SelectDataTask","fetchStatus",e);
			} catch (IOException e) {
				Log.e("SelectDataTask","fetchStatus",e);
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
