package jp.redmine.redmineclient.service;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

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
	public static boolean fetchProject(DatabaseCacheHelper helper
			, SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error
			, int offset
			, int limit){
		final RedmineProjectModel model = new RedmineProjectModel(helper);
		final RedmineConnection connection = client.getConnection();
		final ParserProject parser = new ParserProject();
		RemoteUrlProjects url = new RemoteUrlProjects();
		url.filterLimit(limit);
		if(offset != 0)
			url.filterOffset(offset);
		Fetcher.fetchData(client, error, client.getUrl(url), new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection, RedmineProject>() {
					public void onData(RedmineConnection con, RedmineProject data) throws SQLException {
						model.refreshItem(con, data);
					}
				});
				Fetcher.setupParserStream(stream, parser);
				parser.parse(connection);
			}
		});
		return parser.getCount() >= limit;
	}
}
