package jp.redmine.redmineclient;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.form.RedmineTimeentryEditForm;
import jp.redmine.redmineclient.intent.TimeEntryIntent;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.task.SelectTimeEntriesPost;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class TimeEntryEditActivity extends OrmLiteBaseActivity<DatabaseCacheHelper>  {
	public TimeEntryEditActivity(){
		super();
	}
	private RedmineTimeentryEditForm form;

	private ProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.timeentryedit);

		form = new RedmineTimeentryEditForm(this);
		form.setupDatabase(getHelper());


		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.menu_settings_loading));
	}

	@Override
	protected void onStart() {
		super.onStart();
		onRefresh(true);
	}

	protected void onRefresh(boolean isFetch){
		TimeEntryIntent intent = new TimeEntryIntent(getIntent());
		int connectionid = intent.getConnectionId();

		form.setupParameter(connectionid, 0);

		RedmineTimeEntry timeentry = new RedmineTimeEntry();
		RedmineTimeEntryModel model = new RedmineTimeEntryModel(getHelper());

		if(intent.getTimeEntryId() != -1){
			try {
				timeentry = model.fetchById(connectionid, intent.getTimeEntryId());
			} catch (SQLException e) {
				Log.e("SelectDataTask","ParserIssue",e);
			}
		}
		form.setValue(timeentry);
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
				TimeEntryIntent intent = new TimeEntryIntent(getIntent());
				int connectionid = intent.getConnectionId();
				RedmineConnection connection = null;
				ConnectionModel mConnection = new ConnectionModel(getApplicationContext());
				connection = mConnection.getItem(connectionid);
				mConnection.finalize();

				RedmineTimeEntry timeentry = new RedmineTimeEntry();
				RedmineTimeEntryModel model = new RedmineTimeEntryModel(getHelper());

				if(intent.getTimeEntryId() != -1){
					try {
						timeentry = model.fetchById(connectionid, intent.getTimeEntryId());
					} catch (SQLException e) {
						Log.e("SelectDataTask","ParserIssue",e);
					}
				}
				form.getValue(timeentry);
				SelectTimeEntriesPost post = new SelectTimeEntriesPost(getHelper(), connection){
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
				if(timeentry.getId() == null){
					timeentry.setIssueId(intent.getIssueId());
				}
				post.execute(timeentry);
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
