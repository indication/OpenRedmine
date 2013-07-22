package jp.redmine.redmineclient;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteFragmentActivity;

import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.form.RedmineIssueCommentForm;
import jp.redmine.redmineclient.form.RedmineIssueViewForm;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.task.SelectIssueJournalPost;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class IssueViewActivity extends OrmLiteFragmentActivity<DatabaseCacheHelper>  {
	public IssueViewActivity(){
		super();
	}
	private RedmineIssueCommentForm formComment;
	private RedmineIssueViewForm form;

	private ProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.issueview);
		form = new RedmineIssueViewForm(this);
		formComment = new RedmineIssueCommentForm(this);
		formComment.buttonOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!formComment.Validate())
					return;
				IssueArgument intent = new IssueArgument();
				intent.setIntent(getIntent());

				RedmineConnection connection = null;
				ConnectionModel mConnection = new ConnectionModel(getApplicationContext());
				connection = mConnection.getItem(intent.getConnectionId());
				mConnection.finalize();

				RedmineJournal journal = new RedmineJournal();
				journal.setIssueId((long)intent.getIssueId());
				formComment.getValue(journal);

				SelectIssueJournalPost post = new SelectIssueJournalPost(getHelper(), connection){
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
							formComment.clear();
						}
					}
				};
				post.execute(journal);
			}
		});

		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.menu_settings_uploading));
	}

	@Override
	protected void onStart() {
		super.onStart();
		IssueArgument intent = new IssueArgument();
		intent.setIntent(getIntent());
		int connectionid = intent.getConnectionId();

		RedmineIssueModel model = new RedmineIssueModel(getHelper());
		RedmineIssue issue = new RedmineIssue();
		Log.d("SelectDataTask","ParserIssue Start");
		try {
			issue = model.fetchById(connectionid, intent.getIssueId());
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserIssue",e);
		}

		form.setValue(issue);
	}

}
