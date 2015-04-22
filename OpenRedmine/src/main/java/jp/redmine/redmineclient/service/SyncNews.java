package jp.redmine.redmineclient.service;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineNewsModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineNews;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserNews;
import jp.redmine.redmineclient.task.Fetcher;
import jp.redmine.redmineclient.task.SelectDataTaskDataHandler;
import jp.redmine.redmineclient.task.SelectDataTaskRedmineConnectionHandler;
import jp.redmine.redmineclient.url.RemoteUrlNews;

public class SyncNews {

	static public void fetchNews(DatabaseCacheHelper helper
			, final SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error
			, long projectid) {
		final RedmineNewsModel model = new RedmineNewsModel(helper);
		RedmineProjectModel mProject = new RedmineProjectModel(helper);
		final ParserNews parser = new ParserNews();
		parser.registerDataCreation(new DataCreationHandler<RedmineProject, RedmineNews>() {
			public void onData(RedmineProject con, RedmineNews data) throws SQLException {
				data.setProject(con);
				model.refreshItem(client.getConnection(), data);
			}
		});
		final RedmineProject item;
		try {
			item = mProject.fetchById(projectid);
		} catch (SQLException e) {
			error.onError(e);
			return;
		}
		RemoteUrlNews url = new RemoteUrlNews();
		url.setProject(item);
		Fetcher.fetchData(client, error, client.getUrl(url), new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				Fetcher.setupParserStream(stream, parser);
				parser.parse(item);
			}
		});
	}
}
