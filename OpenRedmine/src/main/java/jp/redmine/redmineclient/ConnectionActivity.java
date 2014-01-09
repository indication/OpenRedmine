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
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.fragment.ActivityInterface;
import jp.redmine.redmineclient.fragment.IssueJump;
import jp.redmine.redmineclient.fragment.IssueList;
import jp.redmine.redmineclient.fragment.ProjectList;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.param.FilterArgument;

public class ConnectionActivity extends OrmLiteFragmentActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	private static final String TAG = ConnectionActivity.class.getSimpleName();
	public ConnectionActivity(){
		super();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ActivityHelper.setupTheme(this);
		super.onCreate(savedInstanceState);
		getSupportActionBar();

		/**
		 * Add fragment on first view only
		 * On rotate, this method would be called with savedInstanceState.
		 */
		if(savedInstanceState != null)
			return;

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.setDisplayShowTitleEnabled(true);

		ConnectionArgument intent = new ConnectionArgument();
		intent.setIntent(getIntent());

		// Project list
		ConnectionArgument argProject = new ConnectionArgument();
		argProject.setArgument();
		argProject.setConnectionId(intent.getConnectionId());

		actionBar.addTab(actionBar.newTab()
				.setText(R.string.ticket_project)
				.setTabListener( ProjectList.newInstance(argProject))
			);


		// Direct issue jump list
		ConnectionArgument argJump = new ConnectionArgument();
		argJump.setArgument();
		argJump.setConnectionId(intent.getConnectionId());

		actionBar.addTab(actionBar.newTab()
				.setText(R.string.ticket_jump)
				.setTabListener( IssueJump.newInstance(argJump))
		);

		RedmineUserModel mUserModel = new RedmineUserModel(getHelper());
		try {
			final RedmineUser user = mUserModel.fetchCurrentUser(intent.getConnectionId());
			if(user != null){
				RedmineFilter filter = new RedmineFilter();
				filter.setConnectionId(intent.getConnectionId());
				filter.setAssigned(user);
				filter.setSort(RedmineFilterSortItem.getFilter(RedmineFilterSortItem.KEY_MODIFIED, false));
				RedmineFilterModel mFilter = new RedmineFilterModel(getHelper());
				RedmineFilter target = mFilter.getSynonym(filter);
				if (target == null) {
					mFilter.insert(filter);
					target = mFilter.getSynonym(filter);
				}

				FilterArgument argIssue = new FilterArgument();
				argIssue.setArgument();
				argIssue.setConnectionId(intent.getConnectionId());
				argIssue.setFilterId(target.getId());
				actionBar.addTab(actionBar.newTab()
						.setText(R.string.ticket_jump)
						.setTabListener(IssueList.newInstance(argIssue))
				);
			}
		} catch (SQLException e) {
			Log.e(TAG,"fetchCurrentUser", e);
		}

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
