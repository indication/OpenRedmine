package jp.redmine.redmineclient.task;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineNewsModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineNews;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserNews;
import jp.redmine.redmineclient.url.RemoteUrlNews;

public class SelectNewsTask extends SelectDataTask<Void,RedmineProject> {
	protected DatabaseCacheHelper helper;
	protected RedmineConnection connection;
	public SelectNewsTask(DatabaseCacheHelper helper, RedmineConnection con){
		this.helper = helper;
		this.connection = con;
	}


	public SelectNewsTask() {
	}

	@Override
	protected Void doInBackground(RedmineProject... params) {
		final RedmineNewsModel model = new RedmineNewsModel(helper);
		final ParserNews parser = new ParserNews();
		parser.registerDataCreation(new DataCreationHandler<RedmineProject,RedmineNews>() {
			public void onData(RedmineProject con,RedmineNews data) throws SQLException {
				data.setProject(con);
				model.refreshItem(connection,data);
			}
		});

		SelectDataTaskRedmineConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		RemoteUrlNews url = new RemoteUrlNews();
		for(final RedmineProject item : params){
			SelectDataTaskDataHandler handler = new SelectDataTaskDataHandler() {
				@Override
				public void onContent(InputStream stream)
						throws XmlPullParserException, IOException, SQLException {
					helperSetupParserStream(stream,parser);
					parser.parse(item);
				}
			};
			url.setProject(item);
			fetchData(client, url, handler);
		}
		return null;
	}

}
