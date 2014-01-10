package jp.redmine.redmineclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.j256.ormlite.android.apptools.OrmLiteFragmentActivity;

import java.sql.SQLException;

import jp.redmine.redmineclient.activity.handler.AttachmentActionHandler;
import jp.redmine.redmineclient.activity.handler.AttachmentActionInterface;
import jp.redmine.redmineclient.activity.handler.ConnectionActionInterface;
import jp.redmine.redmineclient.activity.handler.ConnectionListHandler;
import jp.redmine.redmineclient.activity.handler.Core.ActivityRegistry;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.activity.handler.IssueViewHandler;
import jp.redmine.redmineclient.activity.handler.TimeEntryHandler;
import jp.redmine.redmineclient.activity.handler.TimeentryActionInterface;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.activity.helper.TabHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.fragment.ActivityInterface;
import jp.redmine.redmineclient.fragment.CategoryList;
import jp.redmine.redmineclient.fragment.IssueList;
import jp.redmine.redmineclient.fragment.VersionList;
import jp.redmine.redmineclient.fragment.WikiList;
import jp.redmine.redmineclient.param.FilterArgument;
import jp.redmine.redmineclient.param.ProjectArgument;

public class ProjectActivity extends OrmLiteFragmentActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	private static final String TAG = ProjectActivity.class.getSimpleName();
	public ProjectActivity(){
		super();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ActivityHelper.setupTheme(this);
		super.onCreate(savedInstanceState);
		getSupportActionBar();
		setContentView(R.layout.fragment_one);

		int target_layout = R.id.fragmentOne;
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.setDisplayShowTitleEnabled(true);

		ProjectArgument intent = new ProjectArgument();
		intent.setIntent(getIntent());

		ProjectArgument argIssueList = new ProjectArgument();
		argIssueList.setArgument();
		argIssueList.setConnectionId(intent.getConnectionId());
		argIssueList.setProjectId(intent.getProjectId());

		actionBar.addTab(actionBar.newTab()
				.setText(R.string.ticket_issue)
				.setTabListener(new TabHelper(IssueList.newInstance(argIssueList), target_layout))
				.setIcon(R.drawable.ic_action_message)
			);



		// current user
		RedmineUserModel mUserModel = new RedmineUserModel(getHelper());
		RedmineFilterModel mFilter = new RedmineFilterModel(getHelper());
		RedmineProjectModel mProjectModel = new RedmineProjectModel(getHelper());
		try {
			RedmineProject project = mProjectModel.fetchById(intent.getProjectId());
			final RedmineUser user = mUserModel.fetchCurrentUser(intent.getConnectionId());
			if(user != null){
				//setup parameter
				RedmineFilter filter = new RedmineFilter();
				filter.setConnectionId(intent.getConnectionId());
				filter.setAssigned(user);
				filter.setProject(project);
				filter.setSort(RedmineFilterSortItem.getFilter(RedmineFilterSortItem.KEY_MODIFIED, false));
				RedmineFilter target = mFilter.getSynonym(filter);
				if (target == null) {
					mFilter.insert(filter);
					target = filter;
				}
				FilterArgument param = new FilterArgument();
				param.setArgument();
				param.setConnectionId(intent.getConnectionId());
				param.setProjectId(intent.getProjectId());
				param.setFilterId(target.getId());

				actionBar.addTab(actionBar.newTab()
						.setText(user.getName())
						.setTabListener(new TabHelper(IssueList.newInstance(param), target_layout))
						.setIcon(R.drawable.ic_action_user)
				);
			}
		} catch (SQLException e) {
			Log.e(TAG, "fetchCurrentUser", e);
		}

		// wiki
		ProjectArgument argWiki = new ProjectArgument();
		argWiki.setArgument();
		argWiki.setConnectionId(intent.getConnectionId());
		argWiki.setProjectId(intent.getProjectId());

		actionBar.addTab(actionBar.newTab()
				.setText(R.string.wiki)
				.setTabListener(new TabHelper(WikiList.newInstance(argWiki), target_layout))
		);


		// version
		ProjectArgument argVersion = new ProjectArgument();
		argVersion.setArgument();
		argVersion.setConnectionId(intent.getConnectionId());
		argVersion.setProjectId(intent.getProjectId());

		actionBar.addTab(actionBar.newTab()
				.setText(R.string.ticket_version)
				.setTabListener(new TabHelper(VersionList.newInstance(argVersion), target_layout))
		);


		// category
		ProjectArgument argCategory = new ProjectArgument();
		argCategory.setArgument();
		argCategory.setConnectionId(intent.getConnectionId());
		argCategory.setProjectId(intent.getProjectId());

		actionBar.addTab(actionBar.newTab()
				.setText(R.string.ticket_category)
				.setTabListener(new TabHelper(CategoryList.newInstance(argCategory), target_layout))
		);
	}

	@SuppressWarnings("unchecked")
	public <T> T getHandler(Class<T> cls){
		ActivityRegistry registry = new ActivityRegistry(){

			@Override
			public FragmentManager getFragment() {
				return getSupportFragmentManager();
			}

			@Override
			public Intent getIntent(Class<?> activity) {
				return new Intent(getApplicationContext(),activity);
			}

			@Override
			public void kickActivity(Intent intent) {
				startActivity(intent);
			}

		};
		if(cls.equals(ConnectionActionInterface.class))
			return (T) new ConnectionListHandler(registry);
		if(cls.equals(WebviewActionInterface.class))
			return (T) new IssueViewHandler(registry);
		if(cls.equals(IssueActionInterface.class))
			return (T) new IssueViewHandler(registry);
		if(cls.equals(TimeentryActionInterface.class))
			return (T) new TimeEntryHandler(registry);
		if(cls.equals(AttachmentActionInterface.class))
			return (T) new AttachmentActionHandler(registry);
		return null;
	}
}
