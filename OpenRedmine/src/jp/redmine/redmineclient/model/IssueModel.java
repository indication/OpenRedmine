package jp.redmine.redmineclient.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.store.RedmineConnectionModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.external.Fetcher;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssues;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;

public class IssueModel extends Connector {

	private int connection_id;
	private Long project_id;

	public IssueModel(Context context, int connectionid, Long projectid) {
		super(context);
		connection_id = connectionid;
		project_id = projectid;
	}


	public List<RedmineIssue> fetchAllData(long offset,long limit){
		final RedmineIssueModel model = new RedmineIssueModel(helperCache);
		List<RedmineIssue> issues = new ArrayList<RedmineIssue>();
		try {
			issues = model.fetchAllById(connection_id, project_id, offset, limit);
		} catch (SQLException e) {
			Log.e("SelectDataTask","doInBackground",e);
		} catch (Throwable e) {
			Log.e("SelectDataTask","doInBackground",e);
		}
		return issues;
	}
	public RedmineIssue fetchItem(int issue_id){
		final RedmineIssueModel model =
			new RedmineIssueModel(helperCache);
		RedmineIssue issue = new RedmineIssue();
		Log.d("SelectDataTask","ParserIssue Start");
		try {
			issue = model.fetchById(connection_id, issue_id);
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserIssue",e);
		}
		return issue;
	}

	public int fetchRemoteData(int offset,int limit){

		final RedmineConnectionModel mConnection =
			new RedmineConnectionModel(helperStore);
		final RedmineProjectModel mProject =
			new RedmineProjectModel(helperCache);
		final IssueModelDataCreationHandler handler =
			new IssueModelDataCreationHandler(helperCache);

		RedmineConnection info = null;
		RedmineProject proj = null;
		Log.d("SelectDataTask","ParserProject Start");
		try {
			info = mConnection.fetchById(connection_id);
			proj = mProject.fetchById(project_id);
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserProject",e);
		}
		RemoteUrlIssues url = new RemoteUrlIssues();
		Fetcher<RedmineProject> fetch = new Fetcher<RedmineProject>();
		ParserIssue parser = new ParserIssue();
		parser.registerDataCreation(handler);

		url.filterProject(String.valueOf(proj.getProjectId()));
		url.filterOffset(offset);
		url.filterLimit(limit);
		Log.d("SelectDataTask","ParserProject Start");
		try {
			fetch.setRemoteurl(url);
			fetch.setParser(parser);
			fetch.fetchData(info,proj);

		} catch (XmlPullParserException e) {
			Log.e("SelectDataTask","fetchIssue",e);
		} catch (IOException e) {
			Log.e("SelectDataTask","fetchIssue",e);
		}
		return parser.getCount();
	}
}
