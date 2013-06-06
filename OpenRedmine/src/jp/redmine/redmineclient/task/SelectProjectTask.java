package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedminePriorityModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTimeActivityModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTimeActivity;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserEnumerationIssuePriority;
import jp.redmine.redmineclient.parser.ParserEnumerationTimeEntryActivity;
import jp.redmine.redmineclient.parser.ParserProject;
import jp.redmine.redmineclient.parser.ParserStatus;
import jp.redmine.redmineclient.parser.ParserTracker;
import jp.redmine.redmineclient.parser.ParserUser;
import jp.redmine.redmineclient.url.RemoteUrlEnumerations;
import jp.redmine.redmineclient.url.RemoteUrlProjects;
import jp.redmine.redmineclient.url.RemoteUrlStatus;
import jp.redmine.redmineclient.url.RemoteUrlTrackers;
import jp.redmine.redmineclient.url.RemoteUrlUsers;
import jp.redmine.redmineclient.url.RemoteUrlEnumerations.EnumerationType;

public class SelectProjectTask extends SelectDataTask<List<RedmineProject>,Integer> {

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
		fetchPriority(client);
		fetchTimeEntryActivity(client);
		do {
			List<RedmineProject> projects = fetchProject(client,offset,limit);
			count = projects.size();
			//TODO
			publishProgress(0, 0);
			if(offset != 0){
				//sleep for server
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					publishError(e);
				}
			}
			offset += limit;
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
	protected void fetchPriority(SelectDataTaskConnectionHandler client){
		final RedminePriorityModel model = new RedminePriorityModel(helper);
		RemoteUrlEnumerations url = new RemoteUrlEnumerations();
		url.setType(EnumerationType.IssuePriorities);

		fetchData(client,connection, url, new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserEnumerationIssuePriority parser = new ParserEnumerationIssuePriority();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedminePriority>() {
					public void onData(RedmineConnection con,RedminePriority data) throws SQLException {
						model.refreshItem(con,data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(connection);
			}
		});
	}
	protected void fetchTimeEntryActivity(SelectDataTaskConnectionHandler client){
		final RedmineTimeActivityModel model = new RedmineTimeActivityModel(helper);
		RemoteUrlEnumerations url = new RemoteUrlEnumerations();
		url.setType(EnumerationType.TimeEntryActivities);

		fetchData(client,connection, url, new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserEnumerationTimeEntryActivity parser = new ParserEnumerationTimeEntryActivity();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineTimeActivity>() {
					public void onData(RedmineConnection con,RedmineTimeActivity data) throws SQLException {
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
