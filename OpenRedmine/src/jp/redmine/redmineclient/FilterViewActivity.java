package jp.redmine.redmineclient;


import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.form.RedmineIssueFilter;
import jp.redmine.redmineclient.intent.ProjectIntent;
import android.os.Bundle;

public class FilterViewActivity extends OrmLiteBaseActivity<DatabaseCacheHelper>  {
	public FilterViewActivity(){
		super();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	private RedmineIssueFilter form;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issuefilter);

		form = new RedmineIssueFilter();

	}

	@Override
	protected void onStart() {
		ProjectIntent intent = new ProjectIntent(getIntent());
		int connectionid = intent.getConnectionId();
		long projectid = intent.getProjectId();
		form.setup(this,getHelper(),connectionid,projectid);
		form.setupEvents();
		super.onStart();
	}
}
