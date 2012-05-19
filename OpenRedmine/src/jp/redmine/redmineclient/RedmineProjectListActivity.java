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
import jp.redmine.redmineclient.external.RemoteUrl.requests;
import jp.redmine.redmineclient.external.RemoteUrl.versions;
import jp.redmine.redmineclient.external.RemoteUrlProjects;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
		SelectDataTask task = new SelectDataTask(this);
		task.execute(id);
	}


	private class SelectDataTask extends AsyncTask<Integer, Integer, List<RedmineProject>> {
		private ProgressDialog dialog;
		private Context parentContext;
		public SelectDataTask(final Context tex){
			parentContext = tex;
		}
		// can use UI thread here
		protected void onPreExecute() {
			dialog = new ProgressDialog(parentContext);
			dialog.setMessage(parentContext.getString(R.string.menu_settings_loading));
			dialog.show();
		}

		@Override
		protected List<RedmineProject> doInBackground(Integer ... params) {
			final RedmineProjectModel model = new RedmineProjectModel(getBaseContext());
			final int id = params[0];

			List<RedmineProject> projects = new ArrayList<RedmineProject>();
			RedmineConnectionModel connection = new RedmineConnectionModel(getBaseContext());


			try {
				ProjectParser parser = new ProjectParser();
				final RedmineConnection info = connection.fetchById(id);

				parser.registerDataCreation(new DataCreationHandler<RedmineProject>() {
					public void onData(RedmineProject data) {
						try {
							RedmineProject project = model.fetchById(id, data.ProjectId());
							if(project.Id() == 0){
								model.insert(data);
							} else {
								if(project.Modified().after(data.Modified())){
									data.Id(project.Id());
									data.RedmineConnection(info);
									model.update(data);
								}
							}
						} catch (SQLException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}

					}
				});



				RemoteUrlProjects url = new RemoteUrlProjects(info.Url(),versions.v130,requests.xml);
				Fetcher fetch = new Fetcher();
				fetch.setIgnoreSSLVerification(true);
				fetch.setRemoteurl(url);
				if(info.Auth()){
					fetch.setAuthentication(info.AuthId(), info.AuthPasswd());
				}
				fetch.fetchData();
				fetch.Parse();


				projects = model.fetchAll(id);
			} catch (SQLException e) {
				Log.e("SelectDataTask","doInBackground",e);
			} catch (XmlPullParserException e) {
				Log.e("SelectDataTask","doInBackground",e);
			} catch (IOException e) {
				Log.e("SelectDataTask","doInBackground",e);
			} catch (Throwable e) {
				Log.e("SelectDataTask","doInBackground",e);
			}

			return projects;
		}


		// can use UI thread here
		@Override
		protected void onPostExecute(List<RedmineProject> b) {
			listAdapter.notifyDataSetInvalidated();
			listAdapter.clear();
			for (RedmineProject i : b){
				listAdapter.add(i);
			}
			listAdapter.notifyDataSetChanged();
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

		}

	}
}
