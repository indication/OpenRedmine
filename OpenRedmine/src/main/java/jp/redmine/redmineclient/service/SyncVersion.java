package jp.redmine.redmineclient.service;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserVersion;
import jp.redmine.redmineclient.task.Fetcher;
import jp.redmine.redmineclient.task.SelectDataTaskDataHandler;
import jp.redmine.redmineclient.task.SelectDataTaskRedmineConnectionHandler;
import jp.redmine.redmineclient.url.RemoteUrlVersion;

public class SyncVersion {
	static public void fetchVersions(final DatabaseCacheHelper helper
			, final SelectDataTaskRedmineConnectionHandler client
			, final Fetcher.ContentResponseErrorHandler error
			, long project_id) throws SQLException {
		final RedmineVersionModel model = new RedmineVersionModel(helper);
		final RedmineProjectModel modelProject = new RedmineProjectModel(helper);
		final RedmineProject project = modelProject.fetchById(project_id);
		RemoteUrlVersion url = new RemoteUrlVersion();
		url.setProject(project);

		Fetcher.fetchData(client, error, client.getUrl(url), new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserVersion parser = new ParserVersion();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection, RedmineProjectVersion>() {
					public void onData(RedmineConnection con, RedmineProjectVersion data) throws SQLException {
						data.setConnectionId(con.getId());
						data.setProject(project);
						model.refreshItem(con, data);
					}
				});
				Fetcher.setupParserStream(stream, parser);
				parser.parse(client.getConnection());
			}
		});
	}
}
