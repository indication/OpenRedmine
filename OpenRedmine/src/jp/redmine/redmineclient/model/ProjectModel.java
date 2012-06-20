package jp.redmine.redmineclient.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.db.store.RedmineConnectionModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.external.DataCreationHandler;
import jp.redmine.redmineclient.external.Fetcher;
import jp.redmine.redmineclient.parser.ParserProject;
import jp.redmine.redmineclient.parser.ParserStatus;
import jp.redmine.redmineclient.parser.ParserTracker;
import jp.redmine.redmineclient.parser.ParserUser;
import jp.redmine.redmineclient.url.RemoteUrlProjects;
import jp.redmine.redmineclient.url.RemoteUrlStatus;
import jp.redmine.redmineclient.url.RemoteUrlTrackers;
import jp.redmine.redmineclient.url.RemoteUrlUsers;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;

public class ProjectModel extends Connector {
	private int connection_id;
	public ProjectModel(Context context, int connectionid) {
		super(context);
		connection_id = connectionid;
	}

	public void fetchRemoteData(){

		final RedmineConnectionModel connection =
			new RedmineConnectionModel(helperStore);

		RedmineConnection info = null;
		Log.d("SelectDataTask","ParserProject Start");
		try {
			info = connection.fetchById(connection_id);
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserProject",e);
		}

		if(info != null) {
			fetchProject(info);
			fetchUsers(info);
			fetchTrackers(info);
			fetchStatus(info);
		}
	}

	public List<RedmineProject> fetchAllData(){
		final RedmineProjectModel model =
			new RedmineProjectModel(helperCache);
		List<RedmineProject> projects = new ArrayList<RedmineProject>();
		try {
			projects = model.fetchAll(connection_id);
		} catch (SQLException e) {
			Log.e("SelectDataTask","doInBackground",e);
		} catch (Throwable e) {
			Log.e("SelectDataTask","doInBackground",e);
		}
		return projects;
	}

	protected void fetchProject(RedmineConnection info){
		final RedmineProjectModel model =
			new RedmineProjectModel(helperCache);
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
			new RedmineUserModel(helperCache);
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
			new RedmineTrackerModel(helperCache);
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
			new RedmineStatusModel(helperCache);
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
