package jp.redmine.redmineclient;

import android.support.v4.app.Fragment;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.activity.TabActivity;
import jp.redmine.redmineclient.activity.pager.CorePage;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.fragment.ActivityInterface;
import jp.redmine.redmineclient.fragment.CategoryList;
import jp.redmine.redmineclient.fragment.IssueList;
import jp.redmine.redmineclient.fragment.VersionList;
import jp.redmine.redmineclient.fragment.WikiList;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.FilterArgument;
import jp.redmine.redmineclient.param.ProjectArgument;

public class ProjectActivity extends TabActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	private static final String TAG = ProjectActivity.class.getSimpleName();
	public ProjectActivity(){
		super();
	}

	@Override
	protected List<CorePage> getTabs(){

		ProjectArgument intent = new ProjectArgument();
		intent.setIntent(getIntent());

		// setup navigation
		try {
			RedmineProjectModel mProject = new RedmineProjectModel(getHelper());
			RedmineProject proj = mProject.fetchById(intent.getProjectId());
			if(proj.getId() != null)
				setTitle(proj.getName());
		} catch (SQLException e) {
			Log.e(TAG, "getTabs", e);
		}

		List<CorePage> list = new ArrayList<CorePage>();
		// Project list
		ProjectArgument argList = new ProjectArgument();
		argList.importArgument(intent);
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

				FilterArgument argUser = new FilterArgument();
				argUser.importArgument(intent);
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
		argWiki.importArgument(intent);
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
		argVersion.importArgument(intent);
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
		argCategory.importArgument(intent);
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

}
