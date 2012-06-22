package jp.redmine.redmineclient;

import jp.redmine.redmineclient.form.RedmineIssueViewForm;
import jp.redmine.redmineclient.model.IssueModel;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class IssueViewActivity extends Activity  {
	public static final String INTENT_INT_CONNECTION_ID = "CONNECTIONID";
	public static final String INTENT_INT_ISSUE_ID = "ISSUEID";

	private IssueModel modelIssue;
	private RedmineIssueViewForm form;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(modelIssue != null){
			modelIssue.finalize();
			modelIssue = null;
		}
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issueview);

		form = new RedmineIssueViewForm(this);

		Intent intent = getIntent();
		int connectionid = intent.getIntExtra(INTENT_INT_CONNECTION_ID, -1);
		int issueid = intent.getIntExtra(INTENT_INT_ISSUE_ID, -1);
		modelIssue = new IssueModel(getApplicationContext(), connectionid,null);

		form.setValue(modelIssue.fetchItem(issueid));

	}
}
