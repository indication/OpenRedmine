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
import jp.redmine.redmineclient.external.DataCreationHandler;
import jp.redmine.redmineclient.external.Fetcher;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssue;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;

public class IssueModel extends Connector {

	private int connection_id;
	private Integer project_id;
	public IssueModel(Context context, int connectionid, Integer projectid) {
		super(context);
		connection_id = connectionid;
		project_id = projectid;
	}


	public List<RedmineIssue> fetchAllData(int offset,int limit){
		final RedmineIssueModel model = new RedmineIssueModel(helperCache);
		List<RedmineIssue> issues = new ArrayList<RedmineIssue>();
		try {
			issues = model.fetchAllById(connection_id, project_id);
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

		final RedmineConnectionModel connection =
			new RedmineConnectionModel(helperStore);
		final RedmineProjectModel project =
			new RedmineProjectModel(helperCache);
		final RedmineIssueModel model =
			new RedmineIssueModel(helperCache);

		RedmineConnection info = null;
		RedmineProject proj = null;
		Log.d("SelectDataTask","ParserProject Start");
		try {
			info = connection.fetchById(connection_id);
			proj = project.fetchById(connection_id, project_id);
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserProject",e);
		}
		RemoteUrlIssue url = new RemoteUrlIssue();
		Fetcher<RedmineProject> fetch = new Fetcher<RedmineProject>();
		ParserIssue parser = new ParserIssue();
		parser.registerDataCreation(new DataCreationHandler<RedmineProject,RedmineIssue>() {
			public void onData(RedmineProject proj,RedmineIssue data) {
				Log.d("ParserIssue","OnData Called");
				try {
					model.refreshItem(proj,data);
				} catch (SQLException e) {
					Log.e("ParserIssue","onData",e);
				}
			}
		});

		url.filterProject(String.valueOf(project_id));
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
