package jp.redmine.redmineclient;

import jp.redmine.redmineclient.form.RedmineIssueViewForm;
import jp.redmine.redmineclient.intent.IssueIntent;
import jp.redmine.redmineclient.model.IssueModel;
import android.app.Activity;
import android.os.Bundle;

public class IssueViewActivity extends Activity  {
	public IssueViewActivity(){
		super();
	}

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


	}

	@Override
	protected void onStart() {
		super.onStart();
		IssueIntent intent = new IssueIntent(getIntent());
		int connectionid = intent.getConnectionId();
		int issueid = intent.getIssueId();
		modelIssue = new IssueModel(getApplicationContext(), connectionid,null);
		form.setValue(modelIssue.fetchItem(issueid));
	}
}
