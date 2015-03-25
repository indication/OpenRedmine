package jp.redmine.redmineclient.service;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserProject;
import jp.redmine.redmineclient.task.Fetcher;
import jp.redmine.redmineclient.task.SelectDataTaskDataHandler;
import jp.redmine.redmineclient.task.SelectDataTaskRedmineConnectionHandler;
import jp.redmine.redmineclient.url.RemoteUrlProjects;

public class SyncProject {
	public static void fetchProjectAll(DatabaseCacheHelper helper
			, SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error){
		int limit = 20;
		int offset = 0;
		int count = 0;

		do {
			List<RedmineProject> projects = fetchProject(helper, client, error, offset, limit);
			count = projects.size();
			if(offset != 0){
				//sleep for server
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			offset += limit;
		} while(count >= limit);
	}
	public static List<RedmineProject> fetchProject(DatabaseCacheHelper helper
			, SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error
			, int offset
			, int limit){
		final RedmineProjectModel model = new RedmineProjectModel(helper);
		RemoteUrlProjects url = new RemoteUrlProjects();
		final RedmineConnection connection = client.getConnection();
		final List<RedmineProject> projects = new ArrayList<RedmineProject>();
		url.filterLimit(limit);
		if(offset != 0)
			url.filterOffset(offset);
		Fetcher.fetchData(client, error, client.getUrl(url), new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserProject parser = new ParserProject();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection, RedmineProject>() {
					public void onData(RedmineConnection con, RedmineProject data) throws SQLException {
						model.refreshItem(con, data);
						projects.add(data);
					}
				});
				Fetcher.setupParserStream(stream, parser);
				parser.parse(connection);
			}
		});
		return projects;
	}
}
