package jp.redmine.redmineclient;

import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineIssueViewForm;
import jp.redmine.redmineclient.intent.IssueIntent;
import android.os.Bundle;
import android.util.Log;

public class IssueViewActivity extends DbBaseActivity  {
	public IssueViewActivity(){
		super();
	}

	private RedmineIssueViewForm form;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issueview);

		form = new RedmineIssueViewForm(this);

	}

	@Override
	protected void onStart() {
		super.onStart();
		IssueIntent intent = new IssueIntent(getIntent());
		int connectionid = intent.getConnectionId();
		int issueid = intent.getIssueId();

		RedmineIssueModel model = new RedmineIssueModel(getHelperCache());
		try {
			RedmineIssue issues;
			issues = model.fetchById(connectionid, issueid);
			form.setValue(issues);
		} catch (SQLException e) {
			Log.e("SelectDataTask","doInBackground",e);
		} catch (Throwable e) {
			Log.e("SelectDataTask","doInBackground",e);
		}
	}
}
