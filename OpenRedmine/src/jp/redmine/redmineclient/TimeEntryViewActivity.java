package jp.redmine.redmineclient;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.adapter.RedmineTimeEntryListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineBaseAdapterListFormHelper;
import jp.redmine.redmineclient.form.RedmineIssueViewForm;
import jp.redmine.redmineclient.intent.IssueIntent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class TimeEntryViewActivity extends OrmLiteBaseActivity<DatabaseCacheHelper>  {
	public TimeEntryViewActivity(){
		super();
	}
	private RedmineIssueViewForm form;
	private RedmineBaseAdapterListFormHelper<RedmineTimeEntryListAdapter> formList;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		formList.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		formList.onRestoreInstanceState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.issueview);

		formList = new RedmineBaseAdapterListFormHelper<RedmineTimeEntryListAdapter>();
		formList.setList((ListView)findViewById(R.id.list));
		formList.setAdapter(new RedmineTimeEntryListAdapter(getHelper()));
		formList.onRestoreInstanceState(savedInstanceState);

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
		Log.d("SelectDataTask","ParserIssue Start");
		try {
			issue = model.fetchById(connectionid, intent.getIssueId());
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserIssue",e);
		}
		if(issue.getId() == null){
			//item is not found
		} else {
			form.setValue(issue);

			formList.adapter.setupParameter(connectionid,issue.getIssueId());
			formList.refresh(isFetch);
			if(formList.adapter.getCount() < 1 && isFetch){
				finish();
			}
		}
	}

}
