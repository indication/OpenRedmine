package jp.redmine.redmineclient;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.adapter.RedmineTimeEntryListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.form.RedmineBaseAdapterListFormHelper;
import jp.redmine.redmineclient.form.RedmineIssueViewForm;
import jp.redmine.redmineclient.intent.IssueIntent;
import jp.redmine.redmineclient.intent.TimeEntryIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
		formList.list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				if(formList.adapter == null)
					return;
				@SuppressWarnings("deprecation")
				RedmineTimeEntry entry = (RedmineTimeEntry)formList.adapter.getItem(position);
				if(entry == null)
					return;

				TimeEntryIntent send = new TimeEntryIntent( getApplicationContext(), TimeEntryEditActivity.class );
				send.setConnectionId(entry.getConnectionId());
				send.setIssueId(entry.getIssueId());
				send.setTimeEntryId(entry.getTimeentryId());
				startActivity(send.getIntent());
			}
		});

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
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu( menu );

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.timeentry_view, menu );
		return true;
	}
	private final int FORM_TIMEENTRY = 1;
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_refresh:
			{
				this.onRefresh(true);
				return true;
			}
			case R.id.menu_access_addnew:
			{
				IssueIntent intent = new IssueIntent( getIntent() );
				TimeEntryIntent send = new TimeEntryIntent( getApplicationContext(), TimeEntryEditActivity.class );
				send.setConnectionId(intent.getConnectionId());
				send.setIssueId(intent.getIssueId());
				startActivityForResult(send.getIntent(),FORM_TIMEENTRY);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case FORM_TIMEENTRY:
			if(resultCode !=RESULT_OK )
				break;
			finish();
			break;
		default:
			break;
		}
	}


}
