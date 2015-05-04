package jp.redmine.redmineclient.service;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineCategoryModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserCategory;
import jp.redmine.redmineclient.task.Fetcher;
import jp.redmine.redmineclient.task.SelectDataTaskDataHandler;
import jp.redmine.redmineclient.task.SelectDataTaskRedmineConnectionHandler;
import jp.redmine.redmineclient.url.RemoteUrlCategory;

public class SyncCategory {
	static public void fetchCategory(final DatabaseCacheHelper helper
			, final SelectDataTaskRedmineConnectionHandler client
			, final Fetcher.ContentResponseErrorHandler error
			, long project_id) throws SQLException {
		final RedmineCategoryModel model = new RedmineCategoryModel(helper);
		final RedmineUserModel modelUser = new RedmineUserModel(helper);
		final RedmineProjectModel modelProject = new RedmineProjectModel(helper);
		final RedmineProject project = modelProject.fetchById(project_id);
		final RemoteUrlCategory url = new RemoteUrlCategory();
		url.setProject(project);

		Fetcher.fetchData(client, error, client.getUrl(url), new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserCategory parser = new ParserCategory();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection, RedmineProjectCategory>() {
					public void onData(RedmineConnection con, RedmineProjectCategory data) throws SQLException {
						data.setConnectionId(con.getId());
						if (data.getAssignTo() != null) {
							data.setAssignTo(modelUser.fetchById(con.getId(), data.getAssignTo().getUserId()));
						}
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
