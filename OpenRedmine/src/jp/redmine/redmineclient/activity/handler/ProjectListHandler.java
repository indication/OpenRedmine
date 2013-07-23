package jp.redmine.redmineclient.activity.handler;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.fragment.IssueList;
import jp.redmine.redmineclient.fragment.ProjectList;
import jp.redmine.redmineclient.param.ProjectArgument;

public class ProjectListHandler extends Core implements ProjectList.OnArticleSelectedListener {

	public ProjectListHandler(FragmentManager manager) {
		super(manager);
	}


	@Override
	public void onArticleSelected(int connectionid, long projectid) {

		ProjectArgument arg = new ProjectArgument();
		arg.setArgument();
		arg.setConnectionId(connectionid);
		arg.setProjectId(projectid);

		FragmentTransaction tran = manager.beginTransaction();
		IssueList fragment = IssueList.newInstance();
		fragment.setArguments(arg.getArgument());
		tran.replace(R.id.fragmentOne, fragment);
		tran.addToBackStack(null);
		tran.commit();
	}

	@Override
	public void onArticleEditSelected(int connectionid) {

	}

}
