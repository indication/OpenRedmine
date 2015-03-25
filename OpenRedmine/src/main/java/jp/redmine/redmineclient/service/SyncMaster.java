package jp.redmine.redmineclient.service;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedminePriorityModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTimeActivityModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTimeActivity;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserEnumerationIssuePriority;
import jp.redmine.redmineclient.parser.ParserEnumerationTimeEntryActivity;
import jp.redmine.redmineclient.parser.ParserStatus;
import jp.redmine.redmineclient.parser.ParserTracker;
import jp.redmine.redmineclient.parser.ParserUser;
import jp.redmine.redmineclient.task.Fetcher;
import jp.redmine.redmineclient.task.SelectDataTaskDataHandler;
import jp.redmine.redmineclient.task.SelectDataTaskRedmineConnectionHandler;
import jp.redmine.redmineclient.url.RemoteUrlEnumerations;
import jp.redmine.redmineclient.url.RemoteUrlStatus;
import jp.redmine.redmineclient.url.RemoteUrlTrackers;
import jp.redmine.redmineclient.url.RemoteUrlUsers;

public class SyncMaster {
	public static void fetchStatus(DatabaseCacheHelper helper
			, SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error){
		final RedmineStatusModel model = new RedmineStatusModel(helper);
		RemoteUrlStatus url = new RemoteUrlStatus();
		final RedmineConnection connection = client.getConnection();

		Fetcher.fetchData(client, error, client.getUrl(url), new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserStatus parser = new ParserStatus();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection, RedmineStatus>() {
					public void onData(RedmineConnection con, RedmineStatus data) throws SQLException {
						model.refreshItem(con, data);
					}
				});
				Fetcher.setupParserStream(stream, parser);
				parser.parse(connection);
			}
		});
	}
	public static void fetchTracker(DatabaseCacheHelper helper
			, SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error){
		final RedmineTrackerModel model = new RedmineTrackerModel(helper);
		RemoteUrlTrackers url = new RemoteUrlTrackers();
		final RedmineConnection connection = client.getConnection();

		Fetcher.fetchData(client, error, client.getUrl(url), new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserTracker parser = new ParserTracker();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection, RedmineTracker>() {
					public void onData(RedmineConnection con, RedmineTracker data) throws SQLException {
						model.refreshItem(con, data);
					}
				});
				Fetcher.setupParserStream(stream, parser);
				parser.parse(connection);
			}
		});
	}
	public static void fetchPriority(DatabaseCacheHelper helper
			, SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error){
		final RedminePriorityModel model = new RedminePriorityModel(helper);
		RemoteUrlEnumerations url = new RemoteUrlEnumerations();
		url.setType(RemoteUrlEnumerations.EnumerationType.IssuePriorities);
		final RedmineConnection connection = client.getConnection();

		Fetcher.fetchData(client, error, client.getUrl(url), new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserEnumerationIssuePriority parser = new ParserEnumerationIssuePriority();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedminePriority>() {
					public void onData(RedmineConnection con,RedminePriority data) throws SQLException {
						model.refreshItem(con,data);
					}
				});
				Fetcher.setupParserStream(stream,parser);
				parser.parse(connection);
			}
		});
	}
	public static void fetchTimeEntryActivity(DatabaseCacheHelper helper
			, SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error){
		final RedmineTimeActivityModel model = new RedmineTimeActivityModel(helper);
		RemoteUrlEnumerations url = new RemoteUrlEnumerations();
		url.setType(RemoteUrlEnumerations.EnumerationType.TimeEntryActivities);
		final RedmineConnection connection = client.getConnection();

		Fetcher.fetchData(client, error, client.getUrl(url), new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserEnumerationTimeEntryActivity parser = new ParserEnumerationTimeEntryActivity();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineTimeActivity>() {
					public void onData(RedmineConnection con,RedmineTimeActivity data) throws SQLException {
						model.refreshItem(con,data);
					}
				});
				Fetcher.setupParserStream(stream,parser);
				parser.parse(connection);
			}
		});
	}
	public static void fetchUsers(DatabaseCacheHelper helper
			, SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error){
		final RedmineUserModel model = new RedmineUserModel(helper);
		RemoteUrlUsers url = new RemoteUrlUsers();
		final RedmineConnection connection = client.getConnection();

		Fetcher.fetchData(client, error, client.getUrl(url), new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserUser parser = new ParserUser();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection, RedmineUser>() {
					public void onData(RedmineConnection con, RedmineUser data) throws SQLException {
						data.setupNameFromSeparated();
						model.refreshItem(con, data);
					}
				});
				Fetcher.setupParserStream(stream, parser);
				parser.parse(connection);
			}
		});
	}
	public static void fetchCurrentUser(DatabaseCacheHelper helper
			, SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error){
		final RedmineUserModel model = new RedmineUserModel(helper);
		RemoteUrlUsers url = new RemoteUrlUsers();
		url.filterCurrentUser();
		final RedmineConnection connection = client.getConnection();

		Fetcher.fetchData(client, error, client.getUrl(url), new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserUser parser = new ParserUser();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection, RedmineUser>() {
					public void onData(RedmineConnection con, RedmineUser data) throws SQLException {
						data.setupNameFromSeparated();
						model.refreshCurrentUser(con, data);
					}
				});
				Fetcher.setupParserStream(stream, parser);
				parser.parse(connection);
			}
		});
	}
}
