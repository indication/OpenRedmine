package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineCategoryModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserCategory;
import jp.redmine.redmineclient.parser.ParserProject;
import jp.redmine.redmineclient.parser.ParserStatus;
import jp.redmine.redmineclient.parser.ParserTracker;
import jp.redmine.redmineclient.parser.ParserUser;
import jp.redmine.redmineclient.parser.ParserVersion;
import jp.redmine.redmineclient.url.RemoteUrlCategory;
import jp.redmine.redmineclient.url.RemoteUrlProjects;
import jp.redmine.redmineclient.url.RemoteUrlStatus;
import jp.redmine.redmineclient.url.RemoteUrlTrackers;
import jp.redmine.redmineclient.url.RemoteUrlUsers;
import jp.redmine.redmineclient.url.RemoteUrlVersion;

public class SelectProjectTask extends SelectDataTask<List<RedmineProject>> {

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
		int limit = 20;
		int offset = 0;
		int count = 0;
		SelectDataTaskConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		fetchStatus(client);
		fetchUsers(client);
		fetchTracker(client);
		do {
			if(count != 0){
				//sleep for server
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					publishError(e);
				}
			}
			List<RedmineProject> projects = fetchProject(client,offset,limit);
			count = projects.size();
			for(RedmineProject project : projects){
				fetchVersions(client,project);
				fetchCategory(client,project);
			}
			offset += limit + 1;
		} while(count >= limit);
		client.close();
		return null;
	}

	protected List<RedmineProject> fetchProject(SelectDataTaskConnectionHandler client, int offset, int limit){
		final RedmineProjectModel model =
			new RedmineProjectModel(helper);
		RemoteUrlProjects url = new RemoteUrlProjects();
		final List<RedmineProject> projects = new ArrayList<RedmineProject>();
		url.filterLimit(limit);
		if(offset != 0)
			url.filterOffset(offset);
		fetchData(client,connection, url, new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserProject parser = new ParserProject();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineProject>() {
					public void onData(RedmineConnection con,RedmineProject data) throws SQLException {
						model.refreshItem(con,data);
						projects.add(data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(connection);
			}
		});
		return projects;
	}
	protected void fetchStatus(SelectDataTaskConnectionHandler client){
		final RedmineStatusModel model = new RedmineStatusModel(helper);
		RemoteUrlStatus url = new RemoteUrlStatus();

		fetchData(client,connection, url, new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserStatus parser = new ParserStatus();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineStatus>() {
					public void onData(RedmineConnection con,RedmineStatus data) throws SQLException {
						model.refreshItem(con,data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(connection);
			}
		});
	}
	protected void fetchTracker(SelectDataTaskConnectionHandler client){
		final RedmineTrackerModel model = new RedmineTrackerModel(helper);
		RemoteUrlTrackers url = new RemoteUrlTrackers();

		fetchData(client,connection, url, new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserTracker parser = new ParserTracker();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineTracker>() {
					public void onData(RedmineConnection con,RedmineTracker data) throws SQLException {
						model.refreshItem(con,data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(connection);
			}
		});
	}
	protected void fetchVersions(SelectDataTaskConnectionHandler client,final RedmineProject project){
		final RedmineVersionModel model = new RedmineVersionModel(helper);
		RemoteUrlVersion url = new RemoteUrlVersion();
		url.setProject(project);

		fetchData(client,connection, url, new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserVersion parser = new ParserVersion();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineProjectVersion>() {
					public void onData(RedmineConnection con,RedmineProjectVersion data) throws SQLException {
						data.setConnectionId(con.getId());
						data.setProject(project);
						model.refreshItem(con,data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(connection);
			}
		});
	}
	protected void fetchCategory(SelectDataTaskConnectionHandler client,final RedmineProject project){
		final RedmineCategoryModel model = new RedmineCategoryModel(helper);
		final RedmineUserModel modelUser = new RedmineUserModel(helper);
		RemoteUrlCategory url = new RemoteUrlCategory();
		url.setProject(project);

		fetchData(client,connection, url, new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserCategory parser = new ParserCategory();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineProjectCategory>() {
					public void onData(RedmineConnection con,RedmineProjectCategory data) throws SQLException {
						data.setConnectionId(con.getId());
						RedmineUser currentuser = null;
						while(currentuser == null){
							currentuser = modelUser.fetchById(con.getId(), data.getAssignTo().getUserId());

						}
						data.setProject(project);
						model.refreshItem(con,data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(connection);
			}
		});
	}
	protected void fetchUsers(SelectDataTaskConnectionHandler client){
		final RedmineUserModel model = new RedmineUserModel(helper);
		RemoteUrlUsers url = new RemoteUrlUsers();

		fetchData(client,connection, url, new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserUser parser = new ParserUser();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineUser>() {
					public void onData(RedmineConnection con,RedmineUser data) throws SQLException {
						model.refreshItem(con,data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(connection);
			}
		});
	}

	@Override
	protected void onErrorRequest(int statuscode) {
	}

	@Override
	protected void onProgress(int max, int proc) {
	}

}
