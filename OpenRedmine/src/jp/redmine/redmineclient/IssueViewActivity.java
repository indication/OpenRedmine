package jp.redmine.redmineclient;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.adapter.RedmineJournalListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineJournalModel;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineBaseAdapterListFormHelper;
import jp.redmine.redmineclient.form.RedmineIssueViewDetailForm;
import jp.redmine.redmineclient.form.RedmineIssueViewForm;
import jp.redmine.redmineclient.intent.IssueIntent;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.task.SelectIssueJournalTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class IssueViewActivity extends OrmLiteBaseActivity<DatabaseCacheHelper>  {
	public IssueViewActivity(){
		super();
	}
	private SelectDataTask task;
	private RedmineIssueViewForm form;
	private RedmineIssueViewDetailForm formDetail;
	private RedmineBaseAdapterListFormHelper<RedmineJournalListAdapter> formList;

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
	@Override
	protected void onDestroy() {
		cancelTask();
		super.onDestroy();
	}
	protected void cancelTask(){
		// cleanup task
		if(task != null && task.getStatus() == Status.RUNNING){
			task.cancel(true);
		}
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.issueview);

		formList = new RedmineBaseAdapterListFormHelper<RedmineJournalListAdapter>();
		formList.setList((ListView)findViewById(R.id.list));
		formList.setHeader(getLayoutInflater().inflate(R.layout.issueviewdetail,null), false);
		formList.setFooter(getLayoutInflater().inflate(R.layout.listview_footer,null), false);
		formList.setAdapter(new RedmineJournalListAdapter(new RedmineJournalModel(getHelper())));
		formList.onRestoreInstanceState(savedInstanceState);

		form = new RedmineIssueViewForm(this);
		formDetail = new RedmineIssueViewDetailForm(formList.viewHeader);
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
		RedmineTimeEntryModel mTimeEntry = new RedmineTimeEntryModel(getHelper());
		RedmineIssue issue = new RedmineIssue();
		BigDecimal hours = new BigDecimal(0);
		Log.d("SelectDataTask","ParserIssue Start");
		try {
			issue = model.fetchById(connectionid, intent.getIssueId());
			hours = mTimeEntry.sumByIssueId(connectionid, intent.getIssueId());
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserIssue",e);
		}
		if(issue.getId() == null){
			if(isFetch){
				onFetchRemote();
			}
		} else {
			form.setValue(issue);
			formDetail.setValue(issue);
			formDetail.setValueTimeEntry(hours);
			formList.setHeaderViewVisible(true);

			formList.adapter.setupParameter(connectionid,issue.getId());
			formList.refresh(isFetch);

			if(formList.adapter.getCount() < 1 && isFetch){
				onFetchRemote();
			}
		}
	}

	protected void onFetchRemote(){
		if(task != null && task.getStatus() == Status.RUNNING)
			return;
		IssueIntent intent = new IssueIntent(getIntent());
		task = new SelectDataTask();
		task.execute(intent.getIssueId());
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
			formList.setFooterViewVisible(true);
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(Void v) {
			formList.setFooterViewVisible(false);
			onRefresh(false);
		}


	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu( menu );

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.issue_view, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_refresh:
			{
				onFetchRemote();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
