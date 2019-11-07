package jp.redmine.redmineclient.task;

import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineJournalModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.parser.IssueModelDataCreationHandler;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssue;

public class SelectIssueJournalPost extends SelectDataPost<Void,RedmineJournal> {
	private final static String TAG = "SelectIssueJournalPost";
	protected DatabaseCacheHelper helper;
	protected RedmineConnection connection;
	public SelectIssueJournalPost(DatabaseCacheHelper helper,RedmineConnection con){
		this.helper = helper;
		this.connection = con;
	}


	public SelectIssueJournalPost() {
	}

	@Override
	protected Void doInBackground(RedmineJournal... params) {
		final ParserIssue parser = new ParserIssue();
		SelectDataTaskDataHandler handler = new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				IssueModelDataCreationHandler handler = new IssueModelDataCreationHandler(helper);
				parser.registerDataCreation(handler);
				helperSetupParserStream(stream, parser);
				parser.parse(connection);
			}
		};

		SelectDataTaskRedmineConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		RedmineJournalModel mJournal = new RedmineJournalModel(helper);
		RemoteUrlIssue url = new RemoteUrlIssue();
		for(final RedmineJournal item : params){
			SelectDataTaskPutHandler puthandler = getPutHandler(item);
			if(item.getIssueId() == null){
				url.setIssueId((Integer)null);

				postData(client, url, handler, puthandler);
			} else {
				url.setIssueId(item.getIssueId().intValue());
				boolean isSuccess = putData(client, url, handler, puthandler);
				if(isSuccess && parser.getCount() < 1){
					try {
						mJournal.refreshItem(connection.getId(), item);
					} catch (SQLException e) {
						Log.e(TAG,"update issue",e);
					}
				}
			}
		}
		return null;
	}

}
