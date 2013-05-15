package jp.redmine.redmineclient;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineIssueEditForm;
import jp.redmine.redmineclient.intent.IssueIntent;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class IssueEditActivity extends OrmLiteBaseActivity<DatabaseCacheHelper>  {
	private static final String TAG = "IssueEditActivity";
	public IssueEditActivity(){
		super();
	}
	private RedmineIssueEditForm form;

	private ProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.issuedetailedit);

		form = new RedmineIssueEditForm(this);
		form.setupDatabase(getHelper());


		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.menu_settings_uploading));
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			onRefresh(true);
		} catch (SQLException e) {
			Log.e(TAG,"onStart",e);
		}
	}

	protected void onRefresh(boolean isFetch) throws SQLException{
		IssueIntent intent = new IssueIntent(getIntent());
		int connectionid = intent.getConnectionId();
		long projectid = 0;

		RedmineIssue issue = new RedmineIssue();
		RedmineIssueModel model = new RedmineIssueModel(getHelper());

		if(intent.getIssueId() != -1){
			issue = model.fetchById(connectionid, intent.getIssueId());
			projectid = issue.getProject().getId();
		} else {
			projectid = intent.getProjectId();
		}

		form.setupParameter(connectionid, projectid);
		form.setValue(issue);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu( menu );

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.edit, menu );
		//if(task != null && task.getStatus() == Status.RUNNING)
		//	menu_refresh.setEnabled(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_save:
			{
				if(!form.Validate())
					return true;

				return true;
			}
			case R.id.menu_delete:
			{

				return true;
			}
			case R.id.menu_cancel:
			{
				this.finish();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}



}
