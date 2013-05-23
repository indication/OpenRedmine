package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserTimeEntry;
import jp.redmine.redmineclient.url.RemoteUrlTimeEntries;

public class SelectTimeEntriesPost extends SelectDataPost<Void,RedmineTimeEntry> {

	protected DatabaseCacheHelper helper;
	protected RedmineConnection connection;
	public SelectTimeEntriesPost(DatabaseCacheHelper helper,RedmineConnection con){
		this.helper = helper;
		this.connection = con;
	}


	public SelectTimeEntriesPost() {
	}

	@Override
	protected Void doInBackground(RedmineTimeEntry... params) {
		final RedmineTimeEntryModel model = new RedmineTimeEntryModel(helper);
		final ParserTimeEntry parser = new ParserTimeEntry();
		parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineTimeEntry>() {
			public void onData(RedmineConnection con,RedmineTimeEntry data) throws SQLException {
				model.refreshItem(con,data);
			}
		});
		SelectDataTaskDataHandler handler = new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				helperSetupParserStream(stream,parser);
				parser.parse(connection);
			}
		};

		SelectDataTaskConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		RemoteUrlTimeEntries url = new RemoteUrlTimeEntries();
		for(final RedmineTimeEntry item : params){
			SelectDataTaskPutHandler puthandler = getPutHandler(item);
			if(item.getTimeentryId() == null){
				url.setId(null);

				postData(client, connection, url, handler, puthandler);
			} else {
				url.setId(item.getTimeentryId());
				putData(client, connection, url, handler, puthandler);
			}
		}
		client.close();
		return null;
	}

	@Override
	protected void onErrorRequest(int statuscode) {

	}

	@Override
	protected void onProgress(int max, int proc) {

	}

}
