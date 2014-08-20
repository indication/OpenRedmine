package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.sql.SQLException;
import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.fragment.form.IssueEditForm;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.task.SelectIssuePost;

public class IssueEdit extends OrmLiteFragment<DatabaseCacheHelper> {
	private static final String TAG = "IssueEdit";

	private IssueEditForm form;
	private ProgressDialog dialog;
	private IssueActionInterface mListener;

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
		return inflater.inflate(R.layout.input_issue, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		form = new IssueEditForm(getView());
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
		mListener = ActivityHandler.getHandler(activity, IssueActionInterface.class);

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
					protected void onPostExecute(List<RedmineIssue> result) {
						super.onPostExecute(result);
						if (dialog.isShowing())
							dialog.dismiss();
						if(isSuccess){
							Toast.makeText(getActivity(), R.string.remote_saved, Toast.LENGTH_LONG).show();
							if(result.size() == 1)
								mListener.onIssueRefreshed(connection.getId(), result.get(0).getIssueId());
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
		}
		return super.onOptionsItemSelected(item);
	}

}
