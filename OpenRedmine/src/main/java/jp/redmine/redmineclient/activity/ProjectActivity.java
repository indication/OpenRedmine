package jp.redmine.redmineclient.activity;

import android.support.v4.app.Fragment;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.pager.CorePage;
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
import jp.redmine.redmineclient.fragment.NewsList;
import jp.redmine.redmineclient.fragment.ProjectDetail;
import jp.redmine.redmineclient.fragment.VersionList;
import jp.redmine.redmineclient.fragment.WikiList;
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
		argList.setArgument();
		argList.importArgument(intent);
		list.add((new CorePage<ProjectArgument>() {
					@Override
					public Fragment getRawFragment(ProjectArgument param) {
						return IssueList.newInstance(param);
					}
				})
				.setParam(argList)
				.setName(getString(R.string.ticket_issue))
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

				FilterArgument argUser = new FilterArgument();
				argUser.setArgument();
				argUser.importArgument(intent);
				argUser.setFilterId(target.getId());
				list.add((new CorePage<FilterArgument>() {
							@Override
							public Fragment getRawFragment(FilterArgument param) {
								return IssueList.newInstance(param);
							}
						})
						.setParam(argUser)
						.setName(user.getName())
						.setIcon(R.drawable.ic_action_user)
				);
			}
		} catch (SQLException e) {
			Log.e(TAG,"fetchCurrentUser", e);
		}
		// project detail
		ProjectArgument argProject = new ProjectArgument();
		argProject.setArgument();
		argProject.importArgument(intent);
		list.add((new CorePage<ProjectArgument>() {
					@Override
					public Fragment getRawFragment(ProjectArgument param) {
						return ProjectDetail.newInstance(param);
					}
				})
				.setParam(argProject)
				.setName(getString(R.string.ticket_project))
				.setIcon(R.drawable.ic_project)
		);

		// wiki
		ProjectArgument argWiki = new ProjectArgument();
		argWiki.setArgument();
		argWiki.importArgument(intent);
		list.add((new CorePage<ProjectArgument>() {
					@Override
					public Fragment getRawFragment(ProjectArgument param) {
						return WikiList.newInstance(param);
					}
				})
				.setParam(argWiki)
				.setName(getString(R.string.wiki))
				.setIcon(R.drawable.ic_text_fields)
		);


		// version
		ProjectArgument argVersion = new ProjectArgument();
		argVersion.setArgument();
		argVersion.importArgument(intent);
		list.add((new CorePage<ProjectArgument>() {
					@Override
					public Fragment getRawFragment(ProjectArgument param) {
						return VersionList.newInstance(param);
					}
				})
				.setParam(argVersion)
				.setName(getString(R.string.ticket_version))
				.setIcon(R.drawable.ic_version)
		);


		// category
		ProjectArgument argCategory = new ProjectArgument();
		argCategory.setArgument();
		argCategory.importArgument(intent);
		list.add((new CorePage<ProjectArgument>() {
					@Override
					public Fragment getRawFragment(ProjectArgument param) {
						return CategoryList.newInstance(param);
					}
				})
				.setParam(argCategory)
				.setName(getString(R.string.ticket_category))
				.setIcon(R.drawable.ic_category)
		);

		// news
		ProjectArgument argNews = new ProjectArgument();
		argNews.setArgument();
		argNews.importArgument(intent);
		list.add((new CorePage<ProjectArgument>() {
					@Override
					public Fragment getRawFragment(ProjectArgument param) {
						return NewsList.newInstance(param);
					}
				})
				.setParam(argNews)
				.setName(getString(R.string.news))
				.setIcon(R.drawable.ic_news)
		);
		return list;
	}

}
