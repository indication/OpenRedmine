package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineJournalModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.parser.IssueModelDataCreationHandler;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssues;

public class SelectIssueJournalTask extends SelectDataTask<RedmineIssue> {

	protected DatabaseCacheHelper helper;
	protected RedmineProject project;
	protected RedmineConnection connection;
	public SelectIssueJournalTask(DatabaseCacheHelper helper,RedmineConnection con,RedmineProject proj){
		this.helper = helper;
		this.project = proj;
		this.connection = con;
	}


	public SelectIssueJournalTask() {
	}

	@Override
	protected List<RedmineIssue> doInBackground(Integer... params) {
		List<RedmineIssue> issues = new ArrayList<RedmineIssue>();
		final ParserIssue parser = new ParserIssue();
		SelectDataTaskDataHandler<RedmineConnection> handler = new SelectDataTaskDataHandler<RedmineConnection>() {
			@Override
			public void onContent(RedmineConnection item, InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				IssueModelDataCreationHandler handler = new IssueModelDataCreationHandler(helper);
				parser.registerDataCreation(handler);
				helperSetupParserStream(stream, parser);
				parser.parse(project);
			}
		};
		RemoteUrlIssue url = new RemoteUrlIssue();
		url.setOption(RemoteUrlIssue.IssueOption.WithJournals);
		for(int param: params){
			url.setIssueId(param);
			fetchData(connection, url, handler);
		}
		return issues;
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
