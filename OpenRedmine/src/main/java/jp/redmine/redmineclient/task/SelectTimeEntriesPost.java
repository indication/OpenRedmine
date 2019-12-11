package jp.redmine.redmineclient.task;

import android.util.Log;

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

public class SelectTimeEntriesPost extends SelectDataPost<Void,RedmineTimeEntry> {
	private final static String TAG = "SelectTimeEntriesPost";
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

		SelectDataTaskRedmineConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		RemoteUrlTimeEntries url = new RemoteUrlTimeEntries();
		for(final RedmineTimeEntry item : params){
			SelectDataTaskPutHandler puthandler = getPutHandler(item);
			if(item.getTimeentryId() == null){
				url.setId(null);

				postData(client, url, handler, puthandler);
			} else {
				url.setId(item.getTimeentryId());
				boolean isSuccess = putData(client, url, handler, puthandler);
				if(isSuccess && parser.getCount() < 1){
					try {
						model.refreshItem(connection, item);
					} catch (SQLException e) {
						Log.e(TAG,"update timeentry",e);
					}
				}
			}
		}
		return null;
	}

}
