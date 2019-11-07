package jp.redmine.redmineclient.task;

import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedminePriorityModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTimeActivityModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.parser.ParserEnumerationIssuePriority;
import jp.redmine.redmineclient.parser.ParserEnumerationTimeEntryActivity;
import jp.redmine.redmineclient.parser.ParserProject;
import jp.redmine.redmineclient.parser.ParserStatus;
import jp.redmine.redmineclient.parser.ParserTracker;
import jp.redmine.redmineclient.parser.ParserUser;
import jp.redmine.redmineclient.url.RemoteUrlEnumerations;
import jp.redmine.redmineclient.url.RemoteUrlEnumerations.EnumerationType;
import jp.redmine.redmineclient.url.RemoteUrlProjects;
import jp.redmine.redmineclient.url.RemoteUrlStatus;
import jp.redmine.redmineclient.url.RemoteUrlTrackers;
import jp.redmine.redmineclient.url.RemoteUrlUsers;

public class SelectProjectTask extends SelectDataTask<Void,RedmineConnection> {

	protected DatabaseCacheHelper helper;
	public SelectProjectTask(DatabaseCacheHelper helper){
		this.helper = helper;
	}


	@SuppressWarnings("unused")
	public SelectProjectTask() {
	}

	@Override
	protected Void doInBackground(RedmineConnection... params) {
		for(RedmineConnection connection : params ){
			int limit = 20;
			int offset = 0;
			int count = 0;
			SelectDataTaskRedmineConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
			fetchStatus(connection,client);
			fetchUsers(connection,client);
			fetchCurrentUser(connection,client);
			fetchTracker(connection,client);
			fetchPriority(connection,client);
			fetchTimeEntryActivity(connection,client);
			do {
				List<RedmineProject> projects = fetchProject(connection,client,offset,limit);
				count = projects.size();
				//TODO
				notifyProgress(0, 0);
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
		}
		return null;
	}

	protected List<RedmineProject> fetchProject(final RedmineConnection connection, SelectDataTaskRedmineConnectionHandler client, int offset, int limit){
		final RedmineProjectModel model =
			new RedmineProjectModel(helper);
		RemoteUrlProjects url = new RemoteUrlProjects();
		final List<RedmineProject> projects = new ArrayList<RedmineProject>();
		url.filterLimit(limit);
		if(offset != 0)
			url.filterOffset(offset);
		fetchData(client, url, stream -> {
			ParserProject parser = new ParserProject();
			parser.registerDataCreation((con, data) -> {
				model.refreshItem(con,data);
				projects.add(data);
			});
			helperSetupParserStream(stream,parser);
			parser.parse(connection);
		});
		return projects;
	}
	protected void fetchStatus(final RedmineConnection connection, SelectDataTaskRedmineConnectionHandler client){
		final RedmineStatusModel model = new RedmineStatusModel(helper);
		RemoteUrlStatus url = new RemoteUrlStatus();

		fetchData(client, url, stream -> {
			ParserStatus parser = new ParserStatus();
			parser.registerDataCreation((con, data) -> model.refreshItem(con,data));
			helperSetupParserStream(stream,parser);
			parser.parse(connection);
		});
	}
	protected void fetchTracker(final RedmineConnection connection, SelectDataTaskRedmineConnectionHandler client){
		final RedmineTrackerModel model = new RedmineTrackerModel(helper);
		RemoteUrlTrackers url = new RemoteUrlTrackers();

		fetchData(client, url, stream -> {
			ParserTracker parser = new ParserTracker();
			parser.registerDataCreation((info, data) -> model.refreshItem(info, data));
			helperSetupParserStream(stream,parser);
			parser.parse(connection);
		});
	}
	protected void fetchPriority(final RedmineConnection connection, SelectDataTaskRedmineConnectionHandler client){
		final RedminePriorityModel model = new RedminePriorityModel(helper);
		RemoteUrlEnumerations url = new RemoteUrlEnumerations();
		url.setType(EnumerationType.IssuePriorities);

		fetchData(client, url, stream -> {
			ParserEnumerationIssuePriority parser = new ParserEnumerationIssuePriority();
			parser.registerDataCreation((con, data) -> model.refreshItem(con,data));
			helperSetupParserStream(stream,parser);
			parser.parse(connection);
		});
	}
	protected void fetchTimeEntryActivity(final RedmineConnection connection, SelectDataTaskRedmineConnectionHandler client){
		final RedmineTimeActivityModel model = new RedmineTimeActivityModel(helper);
		RemoteUrlEnumerations url = new RemoteUrlEnumerations();
		url.setType(EnumerationType.TimeEntryActivities);

		fetchData(client, url, stream -> {
			ParserEnumerationTimeEntryActivity parser = new ParserEnumerationTimeEntryActivity();
			parser.registerDataCreation((con, data) -> model.refreshItem(con,data));
			helperSetupParserStream(stream,parser);
			parser.parse(connection);
		});
	}
	protected void fetchUsers(final RedmineConnection connection, SelectDataTaskRedmineConnectionHandler client){
		final RedmineUserModel model = new RedmineUserModel(helper);
		RemoteUrlUsers url = new RemoteUrlUsers();

		fetchData(client, url, stream -> {
			ParserUser parser = new ParserUser();
			parser.registerDataCreation((con, data) -> {
				data.setupNameFromSeparated();
				model.refreshItem(con,data);
			});
			helperSetupParserStream(stream,parser);
			parser.parse(connection);
		});
	}
	protected void fetchCurrentUser(final RedmineConnection connection, SelectDataTaskRedmineConnectionHandler client){
		final RedmineUserModel model = new RedmineUserModel(helper);
		RemoteUrlUsers url = new RemoteUrlUsers();
		url.filterCurrentUser();

		fetchData(client, url, stream -> {
			ParserUser parser = new ParserUser();
			parser.registerDataCreation((con, data) -> {
				data.setupNameFromSeparated();
				model.refreshCurrentUser(con,data);
			});
			helperSetupParserStream(stream,parser);
			parser.parse(connection);
		});
	}

}
