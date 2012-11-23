package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserProject;
import jp.redmine.redmineclient.parser.ParserStatus;
import jp.redmine.redmineclient.parser.ParserTracker;
import jp.redmine.redmineclient.parser.ParserUser;
import jp.redmine.redmineclient.url.RemoteUrlProjects;
import jp.redmine.redmineclient.url.RemoteUrlStatus;
import jp.redmine.redmineclient.url.RemoteUrlTrackers;
import jp.redmine.redmineclient.url.RemoteUrlUsers;

public class SelectProjectTask extends SelectDataTask<RedmineProject> {

	protected DatabaseCacheHelper helper;
	protected RedmineConnection connection;
	public SelectProjectTask(DatabaseCacheHelper helper,RedmineConnection con){
		this.helper = helper;
		this.connection = con;
	}


	public SelectProjectTask() {
	}

	@Override
	protected List<RedmineProject> doInBackground(Integer... params) {
		fetchProject();
		fetchStatus();
		fetchUsers();
		fetchTracker();
		return null;
	}

	protected void fetchProject(){
		final RedmineProjectModel model =
			new RedmineProjectModel(helper);
		RemoteUrlProjects url = new RemoteUrlProjects();

		fetchData(connection, url, new SelectDataTaskDataHandler<RedmineConnection>() {
			@Override
			public void onContent(RedmineConnection item, InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserProject parser = new ParserProject();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineProject>() {
					public void onData(RedmineConnection con,RedmineProject data) throws SQLException {
						model.refreshItem(con,data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(item);
			}
		});
	}
	protected void fetchStatus(){
		final RedmineStatusModel model = new RedmineStatusModel(helper);
		RemoteUrlStatus url = new RemoteUrlStatus();

		fetchData(connection, url, new SelectDataTaskDataHandler<RedmineConnection>() {
			@Override
			public void onContent(RedmineConnection item, InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserStatus parser = new ParserStatus();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineStatus>() {
					public void onData(RedmineConnection con,RedmineStatus data) throws SQLException {
						model.refreshItem(con,data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(item);
			}
		});
	}
	protected void fetchTracker(){
		final RedmineTrackerModel model = new RedmineTrackerModel(helper);
		RemoteUrlTrackers url = new RemoteUrlTrackers();

		fetchData(connection, url, new SelectDataTaskDataHandler<RedmineConnection>() {
			@Override
			public void onContent(RedmineConnection item, InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserTracker parser = new ParserTracker();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineTracker>() {
					public void onData(RedmineConnection con,RedmineTracker data) throws SQLException {
						model.refreshItem(con,data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(item);
			}
		});
	}
	protected void fetchUsers(){
		final RedmineUserModel model = new RedmineUserModel(helper);
		RemoteUrlUsers url = new RemoteUrlUsers();

		fetchData(connection, url, new SelectDataTaskDataHandler<RedmineConnection>() {
			@Override
			public void onContent(RedmineConnection item, InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserUser parser = new ParserUser();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineUser>() {
					public void onData(RedmineConnection con,RedmineUser data) throws SQLException {
						model.refreshItem(con,data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(item);
			}
		});
	}

	@Override
	protected void onErrorRequest(int statuscode) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void onProgress(int max, int proc) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
