package jp.redmine.redmineclient.task;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserTimeEntry;
import jp.redmine.redmineclient.url.RemoteUrlTimeEntries;

public class SelectTimeEntriesTask extends SelectDataTask<Void,Integer> {
	private final static int LIMIT = 50;

	protected DatabaseCacheHelper helper;
	protected RedmineConnection connection;
	public SelectTimeEntriesTask(DatabaseCacheHelper helper,RedmineConnection con){
		this.helper = helper;
		this.connection = con;
	}


	public SelectTimeEntriesTask() {
	}

	@Override
	protected Void doInBackground(Integer... params) {
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

		SelectDataTaskRedmineConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		RemoteUrlTimeEntries url = new RemoteUrlTimeEntries();
		for(int item : params){
			int offset = 0;
			url.filterLimit(LIMIT);
			url.filterIssue(String.valueOf(item));
			do {
				url.filterOffset(offset);
				fetchData(client, url, handler);
				offset += parser.getCount() + 1;
			} while(parser.getCount() == LIMIT);
		}
		return null;
	}

}
