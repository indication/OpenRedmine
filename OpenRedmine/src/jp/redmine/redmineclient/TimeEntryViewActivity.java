package jp.redmine.redmineclient;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteFragmentActivity;

import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineIssueViewForm;
import jp.redmine.redmineclient.intent.IssueIntent;
import android.os.Bundle;
import android.util.Log;

public class TimeEntryViewActivity extends OrmLiteFragmentActivity<DatabaseCacheHelper>  {
	public TimeEntryViewActivity(){
		super();
	}
	private RedmineIssueViewForm form;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.timeentrylist);

		form = new RedmineIssueViewForm(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		onRefresh(true);
	}

	protected void onRefresh(boolean isFetch){
		IssueIntent intent = new IssueIntent(getIntent());
		int connectionid = intent.getConnectionId();

		RedmineIssueModel model = new RedmineIssueModel(getHelper());
		RedmineIssue issue = new RedmineIssue();
		try {
			issue = model.fetchById(connectionid, intent.getIssueId());
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserIssue",e);
		}
		if(issue.getId() == null){
			//item is not found
		} else {
			form.setValue(issue);
		}
	}


}
