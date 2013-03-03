package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.parser.IssueModelDataCreationHandler;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssue;

public class SelectIssueJournalTask extends SelectDataTask<Void> {

	protected DatabaseCacheHelper helper;
	protected RedmineConnection connection;
	public SelectIssueJournalTask(DatabaseCacheHelper helper,RedmineConnection con,RedmineProject proj){
		this.helper = helper;
		this.connection = con;
	}


	public SelectIssueJournalTask() {
	}

	@Override
	protected Void doInBackground(Integer... params) {
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
		RemoteUrlIssue url = new RemoteUrlIssue();
		url.setOption(RemoteUrlIssue.IssueOption.WithJournals);
		SelectDataTaskConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		for(int param: params){
			url.setIssueId(param);
			fetchData(client,connection, url, handler);
		}
		client.close();
		return null;
	}

	@Override
	protected void onErrorRequest(int statuscode) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void onProgress(int max, int proc) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
