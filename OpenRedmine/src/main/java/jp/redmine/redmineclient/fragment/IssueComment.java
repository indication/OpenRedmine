package jp.redmine.redmineclient.fragment;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.form.RedmineIssueCommentForm;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.task.SelectIssueJournalPost;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class IssueComment extends OrmLiteFragment<DatabaseCacheHelper> {

	private RedmineIssueCommentForm form;
	private ProgressDialog dialog;

	public IssueComment(){
		super();
	}

	static public IssueComment newInstance(IssueArgument intent){
		IssueComment instance = new IssueComment();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.issuecomment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		form = new RedmineIssueCommentForm(getView());

		form.buttonOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!form.Validate())
					return;
				IssueArgument intent = new IssueArgument();
				intent.setArgument(getArguments());

				RedmineConnection connection = null;
				ConnectionModel mConnection = new ConnectionModel(getActivity());
				connection = mConnection.getItem(intent.getConnectionId());
				mConnection.finalize();

				RedmineJournal journal = new RedmineJournal();
				journal.setIssueId((long)intent.getIssueId());
				form.getValue(journal);

				SelectIssueJournalPost post = new SelectIssueJournalPost(getHelper(), connection){
					private boolean isSuccess = true;
					@Override
					protected void onError(Exception lasterror) {
						isSuccess = false;
						ActivityHelper.toastRemoteError(getActivity(), ActivityHelper.ERROR_APP);
						super.onError(lasterror);
					}
					@Override
					protected void onErrorRequest(int statuscode) {
						isSuccess = false;
						ActivityHelper.toastRemoteError(getActivity(), statuscode);
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
							Toast.makeText(getActivity(), R.string.remote_saved, Toast.LENGTH_LONG).show();
							form.clear();
						}
					}
				};
				post.execute(journal);
			}
		});

		dialog = new ProgressDialog(getActivity());
		dialog.setMessage(getString(R.string.menu_settings_uploading));

	}

}
