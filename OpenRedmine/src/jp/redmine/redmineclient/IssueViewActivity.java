package jp.redmine.redmineclient;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.adapter.RedmineJournalListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineJournalModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineIssueViewDetailForm;
import jp.redmine.redmineclient.form.RedmineIssueViewForm;
import jp.redmine.redmineclient.intent.IssueIntent;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.task.SelectIssueJournalTask;
import android.os.Bundle;
import android.util.Log;

public class IssueViewActivity extends OrmLiteBaseActivity<DatabaseCacheHelper>  {
	public IssueViewActivity(){
		super();
	}
	private SelectDataTask task;
	private RedmineIssueViewForm form;
	private RedmineIssueViewDetailForm formDetail;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issueview);

		form = new RedmineIssueViewForm(this);
		formDetail = new RedmineIssueViewDetailForm(form.viewHeader);

	}

	@Override
	protected void onStart() {
		super.onStart();
		IssueIntent intent = new IssueIntent(getIntent());
		int connectionid = intent.getConnectionId();
		int issueid = intent.getIssueId();

		RedmineIssueModel model = new RedmineIssueModel(getHelper());
		RedmineIssue issue = new RedmineIssue();
		Log.d("SelectDataTask","ParserIssue Start");
		try {
			issue = model.fetchById(connectionid, issueid);
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserIssue",e);
		}
		form.setValue(issue);
		formDetail.setValue(issue);
		form.setListHeaderViewVisible(true);

		task = new SelectDataTask();
		task.execute(issueid);
	}

	private class SelectDataTask extends SelectIssueJournalTask{
		public SelectDataTask() {
			super();
			helper = getHelper();
			IssueIntent intent = new IssueIntent(getIntent());
			int connectionid = intent.getConnectionId();
			ConnectionModel mConnection = new ConnectionModel(getApplicationContext());
			connection = mConnection.getItem(connectionid);
			mConnection.finalize();
		}
		// can use UI thread here
		@Override
		protected void onPreExecute() {
			form.setListFooterViewVisible(true);
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(List<RedmineIssue> issues) {
			form.setListFooterViewVisible(false);
			IssueIntent intent = new IssueIntent(getIntent());
			RedmineJournalListAdapter listAdapter = new RedmineJournalListAdapter(new RedmineJournalModel(getHelper())
					,intent.getConnectionId(),(long)intent.getIssueId());
			form.list.setAdapter(listAdapter);


		}


	}
}
