package jp.redmine.redmineclient.fragment;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.RedmineIssueEditForm;
import jp.redmine.redmineclient.fragment.IssueView.OnArticleSelectedListener;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.task.SelectIssuePost;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class IssueEdit extends OrmLiteFragment<DatabaseCacheHelper> {
	private static final String TAG = "IssueEdit";

	private RedmineIssueEditForm form;
	private ProgressDialog dialog;
	private OnArticleSelectedListener mListener;

	public IssueEdit(){
		super();
	}


	static public IssueEdit newInstance(IssueArgument intent){
		IssueEdit instance = new IssueEdit();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.issuedetailedit, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		form = new RedmineIssueEditForm(getView());
		form.setupDatabase(getHelper());


		dialog = new ProgressDialog(getActivity());
		dialog.setMessage(getString(R.string.menu_settings_uploading));
	}
	@Override
	public void onStart() {
		super.onStart();
		try {
			onRefresh(true);
		} catch (SQLException e) {
			Log.e(TAG,"onStart",e);
		}
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof ActivityInterface){
			mListener = ((ActivityInterface)activity).getHandler(OnArticleSelectedListener.class);
		}
		if(mListener == null) {
			//setup empty events
			mListener = new OnArticleSelectedListener() {

				@Override
				public void onIssueFilterList(int connectionId, int filterid) {}
				@Override
				public void onIssueList(int connectionId, long projectId) {}
				@Override
				public void onIssueSelected(int connectionid, int issueid) {}
				@Override
				public void onIssueEdit(int connectionid, int issueid) {}
				@Override
				public void onIssueRefreshed(int connectionid, int issueid) {}
				@Override
				public void onIssueAdd(int connectionId, long projectId) {}
			};
		}

	}

	protected void onRefresh(boolean isFetch) throws SQLException{
		IssueArgument intent = new IssueArgument();
		intent.setArgument(getArguments());
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate( R.menu.edit, menu );
		super.onCreateOptionsMenu(menu, inflater);
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
				intent.setArgument(getArguments());
				int connectionid = intent.getConnectionId();
				RedmineConnection connection = null;
				ConnectionModel mConnection = new ConnectionModel(getActivity());
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
							getFragmentManager().popBackStack();
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
				getFragmentManager().popBackStack();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

}
