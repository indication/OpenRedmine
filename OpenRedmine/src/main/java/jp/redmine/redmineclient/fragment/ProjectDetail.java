package jp.redmine.redmineclient.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.WebViewActivity;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.store.RedmineConnectionModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.fragment.form.ProjectForm;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.ProjectArgument;
import jp.redmine.redmineclient.param.WebArgument;

public class ProjectDetail extends OrmLiteFragment<DatabaseCacheHelper> {
	private static final String TAG = ProjectDetail.class.getSimpleName();

	private WebviewActionInterface mListener;

	public ProjectDetail(){
		super();
	}

	static public ProjectDetail newInstance(ProjectArgument arg){
		ProjectDetail fragment = new ProjectDetail();
		fragment.setArguments(arg.getArgument());
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_project, container, false);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mListener = ActivityHandler.getHandler(getActivity(), WebviewActionInterface.class);

		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());

		RedmineProjectModel mProject = new RedmineProjectModel(getHelper());
		RedmineConnection connection = ConnectionModel.getItem(getActivity(), intent.getConnectionId());

		RedmineProject project = null;
		try {
			project = mProject.fetchById(intent.getProjectId());
		} catch (SQLException e) {
			Log.e(TAG, "onActivityCreated", e);
		}

		ProjectForm form = new ProjectForm(getView());
		form.setupWebView(mListener);
		form.setValue(connection, project);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate( R.menu.web, menu );
		super.onCreateOptionsMenu(menu, inflater);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_web:
			{
				ProjectArgument input = new ProjectArgument();
				input.setArgument(getArguments());
				RedmineProject project = null;
				RedmineProjectModel mProject = new RedmineProjectModel(getHelper());
				try {
					project = mProject.fetchById(input.getProjectId());
				} catch (SQLException e) {
					Log.e(TAG,"onOptionsItemSelected",e);
					return false;
				}
				WebArgument intent = new WebArgument();
				intent.setIntent(getActivity().getApplicationContext(), WebViewActivity.class);
				intent.importArgument(input);
				intent.setUrl("/projects/"
						+ ((project == null || project.getName() == null) ? "" : project.getName())
						+ ""
				);
				getActivity().startActivity(intent.getIntent());
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
