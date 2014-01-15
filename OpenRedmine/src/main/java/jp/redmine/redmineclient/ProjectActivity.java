package jp.redmine.redmineclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.j256.ormlite.android.apptools.OrmLiteFragmentActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import jp.redmine.redmineclient.activity.pager.CorePage;
import jp.redmine.redmineclient.activity.pager.CorePager;
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
		setContentView(R.layout.fragment_pager);

		List<CorePage> list = getTabs();

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.setDisplayShowTitleEnabled(true);

		final ViewPager mPager = (ViewPager) findViewById(R.id.pager);

		/** Defining a listener for pageChange */
		ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				actionBar.setSelectedNavigationItem(position);
			}
		};

		/** Setting the pageChange listner to the viewPager */
		mPager.setOnPageChangeListener(pageChangeListener);
		mPager.setAdapter(new CorePager(getSupportFragmentManager(), list));

		ActionBar.TabListener listener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
				mPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

			}

			@Override
			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

			}
		};

		for(CorePage item : list){
			ActionBar.Tab tab = actionBar.newTab();
			tab.setText(item.getName());
			tab.setTabListener(listener);
			if (item.getIcon() != null)
				tab.setIcon(item.getIcon());
			actionBar.addTab(tab);
		}

	}

	protected List<CorePage> getTabs(){

		ProjectArgument intent = new ProjectArgument();
		intent.setIntent(getIntent());

		ProjectArgument arg = new ProjectArgument();
		arg.setArgument();
		arg.setConnectionId(intent.getConnectionId());
		arg.setProjectId(intent.getProjectId());



		List<CorePage> list = new ArrayList<CorePage>();
		// Project list
		ProjectArgument argList = new ProjectArgument();
		argList.setArgument(arg.getArgument(), true);
		list.add((new CorePage<ProjectArgument>() {
			@Override
			public Fragment getRawFragment() {
				return IssueList.newInstance(getParam());
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

		// current user
		RedmineUserModel mUserModel = new RedmineUserModel(getHelper());
		RedmineFilterModel mFilter = new RedmineFilterModel(getHelper());
		RedmineProjectModel mProjectModel = new RedmineProjectModel(getHelper());
		try {
			RedmineProject project = mProjectModel.fetchById(arg.getProjectId());
			final RedmineUser user = mUserModel.fetchCurrentUser(arg.getConnectionId());
			if(user != null){
				//setup parameter
				RedmineFilter filter = new RedmineFilter();
				filter.setConnectionId(arg.getConnectionId());
				filter.setAssigned(user);
				filter.setProject(project);
				filter.setSort(RedmineFilterSortItem.getFilter(RedmineFilterSortItem.KEY_MODIFIED, false));
				RedmineFilter target = mFilter.getSynonym(filter);
				if (target == null) {
					mFilter.insert(filter);
					target = filter;
				}

				FilterArgument argUser = new FilterArgument();
				argUser.setArgument(arg.getArgument(), true);
				argUser.setFilterId(target.getId());
				list.add((new CorePage<FilterArgument>() {
					@Override
					public Fragment getRawFragment() {
						return IssueList.newInstance(getParam());
					}

					@Override
					public CharSequence getName() {
						return user.getName();
					}

					@Override
					public Integer getIcon() {
						return R.drawable.ic_action_user;
					}
				}).setParam(argUser));
			}
		} catch (SQLException e) {
			Log.e(TAG,"fetchCurrentUser", e);
		}

		// wiki
		ProjectArgument argWiki = new ProjectArgument();
		argWiki.setArgument(arg.getArgument(), true);
		list.add((new CorePage<ProjectArgument>() {
			@Override
			public Fragment getRawFragment() {
				return WikiList.newInstance(getParam());
			}

			@Override
			public CharSequence getName() {
				return getString(R.string.wiki);
			}
		}).setParam(argWiki));


		// version
		ProjectArgument argVersion = new ProjectArgument();
		argVersion.setArgument(arg.getArgument(), true);
		list.add((new CorePage<ProjectArgument>() {
			@Override
			public Fragment getRawFragment() {
				return VersionList.newInstance(getParam());
			}

			@Override
			public CharSequence getName() {
				return getString(R.string.ticket_version);
			}
		}).setParam(argVersion));


		// category
		ProjectArgument argCategory = new ProjectArgument();
		argCategory.setArgument(arg.getArgument(), true);
		list.add((new CorePage<ProjectArgument>() {
			@Override
			public Fragment getRawFragment() {
				return CategoryList.newInstance(getParam());
			}

			@Override
			public CharSequence getName() {
				return getString(R.string.ticket_category);
			}
		}).setParam(argCategory));

		return list;
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
