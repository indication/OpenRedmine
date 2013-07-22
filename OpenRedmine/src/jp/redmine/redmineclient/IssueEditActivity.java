package jp.redmine.redmineclient;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.RedmineIssueEditForm;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.task.SelectIssuePost;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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
		IssueArgument intent = new IssueArgument();
		intent.setIntent(getIntent());
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
				IssueArgument intent = new IssueArgument();
				intent.setIntent(getIntent());
				int connectionid = intent.getConnectionId();
				RedmineConnection connection = null;
				ConnectionModel mConnection = new ConnectionModel(getApplicationContext());
				connection = mConnection.getItem(connectionid);
				mConnection.finalize();

				RedmineIssue issue = new RedmineIssue();
				RedmineIssueModel model = new RedmineIssueModel(getHelper());

				if(intent.getIssueId() != -1){
					try {
						issue = model.fetchById(connectionid, intent.getIssueId());
					} catch (SQLException e) {
						Log.e("SelectDataTask","ParserIssue",e);
					}
				} else {
					RedmineProject project = null;
					RedmineProjectModel mProject = new RedmineProjectModel(getHelper());
					try {
						project = mProject.fetchById(intent.getProjectId());
					} catch (SQLException e) {
						Log.e("SelectDataTask","Project",e);
					}
					if(project != null)
						issue.setProject(project);
				}
				form.getValue(issue);
				SelectIssuePost post = new SelectIssuePost(getHelper(), connection){
					private boolean isSuccess = true;
					@Override
					protected void onError(Exception lasterror) {
						isSuccess = false;
						ActivityHelper.toastRemoteError(getApplicationContext(), ActivityHelper.ERROR_APP);
						super.onError(lasterror);
					}
					@Override
					protected void onErrorRequest(int statuscode) {
						isSuccess = false;
						ActivityHelper.toastRemoteError(getApplicationContext(), statuscode);
						super.onErrorRequest(statuscode);
					}
					@Override
					protected void onPreExecute() {
						dialog.show();
						super.onPreExecute();
					}
					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						if (dialog.isShowing())
							dialog.dismiss();
						if(isSuccess){
							Toast.makeText(getApplicationContext(), R.string.remote_saved, Toast.LENGTH_LONG).show();
							setResult(RESULT_OK);
							finish();
						}
					}
				};
				post.execute(issue);

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
