package jp.redmine.redmineclient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.RedmineConnectionModel;
import jp.redmine.redmineclient.db.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.external.DataCreationHandler;
import jp.redmine.redmineclient.external.Fetcher;
import jp.redmine.redmineclient.external.ProjectParser;
import jp.redmine.redmineclient.external.RemoteUrlProjects;
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
			final RedmineProjectModel model = new RedmineProjectModel(getBaseContext());
			final int id = params[0];

			RedmineConnectionModel connection = new RedmineConnectionModel(getBaseContext());

			Log.d("SelectDataTask","ProjectParser Start");

			try {
				ProjectParser parser = new ProjectParser();
				final RedmineConnection info = connection.fetchById(id);

				parser.registerDataCreation(new DataCreationHandler<RedmineProject>() {
					public void onData(RedmineProject data) {
						Log.d("SelectDataTask","OnData Called");
						try {
							RedmineProject project = model.fetchById(id, data.ProjectId());
							if(project.Id() == null){
								data.RedmineConnection(info);
								model.insert(data);
							} else {
								if(project.Modified().after(data.Modified())){
									data.Id(project.Id());
									data.RedmineConnection(info);
									model.update(data);
								}
							}
						} catch (SQLException e) {
							Log.e("SelectDataTask","onData",e);
						}

					}
				});

				RemoteUrlProjects url = new RemoteUrlProjects();
				Fetcher fetch = new Fetcher();
				fetch.setRemoteurl(url);
				fetch.setParser(parser);
				fetch.fetchData(info);
				fetch.Parse();

			} catch (SQLException e) {
				Log.e("SelectDataTask","doInBackground",e);
			} catch (XmlPullParserException e) {
				Log.e("SelectDataTask","doInBackground",e);
			} catch (IOException e) {
				Log.e("SelectDataTask","doInBackground",e);
			} catch (Throwable e) {
				Log.e("SelectDataTask","doInBackground",e);
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
