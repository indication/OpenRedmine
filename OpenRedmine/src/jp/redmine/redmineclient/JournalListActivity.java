package jp.redmine.redmineclient;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.form.RedmineIssueViewForm;
import jp.redmine.redmineclient.intent.IssueIntent;
import android.app.Activity;
import android.os.Bundle;

public class JournalListActivity extends OrmLiteBaseActivity<DatabaseCacheHelper>  {
	public JournalListActivity(){
		super();
	}

	private RedmineIssueViewForm form;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.journallist);

		form = new RedmineIssueViewForm(this);


	}

	@Override
	protected void onStart() {
		super.onStart();
		IssueIntent intent = new IssueIntent(getIntent());
		int connectionid = intent.getConnectionId();
		int issueid = intent.getIssueId();

	}
}
