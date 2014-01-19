package jp.redmine.redmineclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.activity.TabActivity;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.activity.handler.IssueViewHandler;
import jp.redmine.redmineclient.activity.pager.CorePage;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.fragment.ActivityInterface;
import jp.redmine.redmineclient.fragment.CategoryList;
import jp.redmine.redmineclient.fragment.Issue;
import jp.redmine.redmineclient.fragment.IssueList;
import jp.redmine.redmineclient.fragment.VersionList;
import jp.redmine.redmineclient.fragment.WikiList;
import jp.redmine.redmineclient.param.FilterArgument;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.param.ProjectArgument;

public class IssueActivity extends TabActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	private static final String TAG = IssueActivity.class.getSimpleName();
	public IssueActivity(){
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected List<CorePage> getTabs(){

		IssueArgument intent = new IssueArgument();
		intent.setIntent(getIntent());

		IssueArgument arg = new IssueArgument();
		arg.setArgument();
		arg.setConnectionId(intent.getConnectionId());
		arg.setProjectId(intent.getProjectId());
		arg.setIssueId(intent.getIssueId());

		// setup navigation
		try {
			RedmineProject proj = null;
			if(intent.getProjectId() < 0){
				RedmineIssueModel mIssue = new RedmineIssueModel(getHelper());
				RedmineIssue issue = mIssue.fetchById(intent.getConnectionId(), intent.getIssueId());
				proj = issue.getProject();
			}
			if(proj == null){
				RedmineProjectModel mProject = new RedmineProjectModel(getHelper());
				proj = mProject.fetchById(intent.getProjectId());
			}
			if(proj != null && proj.getId() != null){
				intent.setProjectId(proj.getId());
				setTitle(proj.getName());
			}
		} catch (SQLException e) {
			Log.e(TAG, "getTabs", e);
		}

		List<CorePage> list = new ArrayList<CorePage>();
		// Issue view
		IssueArgument argList = new IssueArgument();
		argList.setArgument(arg.getArgument(), true);
		list.add((new CorePage<IssueArgument>() {
			@Override
			public Fragment getRawFragment() {
				return Issue.newInstance(getParam());
			}

			@Override
			public CharSequence getName() {
				return getString(R.string.ticket_issue);
			}

			@Override
			public Integer getIcon() {
				return R.drawable.ic_action_message;
			}
		}).setParam(argList));

		return list;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:

				IssueArgument intent = new IssueArgument();
				intent.setIntent(getIntent());
				if(intent.getProjectId() > 0){
					IssueActionInterface handler = getHandler(IssueActionInterface.class);
					handler.onIssueList(intent.getConnectionId(), intent.getProjectId());
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
