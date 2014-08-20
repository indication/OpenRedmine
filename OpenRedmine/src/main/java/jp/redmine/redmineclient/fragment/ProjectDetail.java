package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.fragment.form.ProjectForm;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.ProjectArgument;

public class ProjectDetail extends OrmLiteFragment<DatabaseCacheHelper> {
	private static final String TAG = ProjectDetail.class.getSimpleName();

	private WebviewActionInterface mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = ActivityHandler.getHandler(activity, WebviewActionInterface.class);

	}
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


		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());

		RedmineProjectModel mProject = new RedmineProjectModel(getHelper());
		RedmineProject project = null;
		try {
			project = mProject.fetchById(intent.getProjectId());
		} catch (SQLException e) {
			Log.e(TAG, "onActivityCreated", e);
		}

		ProjectForm form = new ProjectForm(getView());
		form.setupWebView(mListener);
		form.setValue(project);
	}

}
