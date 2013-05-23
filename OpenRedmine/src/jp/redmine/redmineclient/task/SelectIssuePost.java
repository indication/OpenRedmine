package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.parser.IssueModelDataCreationHandler;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssue;

public class SelectIssuePost extends SelectDataPost<Void,RedmineIssue> {

	protected DatabaseCacheHelper helper;
	protected RedmineConnection connection;
	public SelectIssuePost(DatabaseCacheHelper helper,RedmineConnection con){
		this.helper = helper;
		this.connection = con;
	}


	public SelectIssuePost() {
	}

	@Override
	protected Void doInBackground(RedmineIssue... params) {
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

		SelectDataTaskConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		RemoteUrlIssue url = new RemoteUrlIssue();
		for(final RedmineIssue item : params){
			SelectDataTaskPutHandler puthandler = getPutHandler(item);
			if(item.getIssueId() == null){
				url.setIssueId((Integer)null);

				postData(client, connection, url, handler, puthandler);
			} else {
				url.setIssueId(item.getIssueId());
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
