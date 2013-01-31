package jp.redmine.redmineclient.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineIssue;

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

}
