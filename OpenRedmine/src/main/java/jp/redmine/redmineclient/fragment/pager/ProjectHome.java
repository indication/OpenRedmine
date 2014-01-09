package jp.redmine.redmineclient.fragment.pager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.fragment.CategoryList;
import jp.redmine.redmineclient.fragment.IssueList;
import jp.redmine.redmineclient.fragment.VersionList;
import jp.redmine.redmineclient.fragment.WikiList;
import jp.redmine.redmineclient.param.FilterArgument;
import jp.redmine.redmineclient.param.ProjectArgument;

public class ProjectHome extends OrmLiteFragment<DatabaseCacheHelper> {
	private static final String TAG = ProjectHome.class.getSimpleName();

	public ProjectHome() {
		super();
	}

	static public ProjectHome newInstance(ProjectArgument intent) {
		ProjectHome instance = new ProjectHome();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		List<CorePager.PageFragment> list = new LinkedList<CorePager.PageFragment>();
		// Project list
		list.add(new CorePager.PageFragment() {
			@Override
			public Fragment getFragment() {
				ProjectArgument arg = new ProjectArgument();
				arg.setArgument(getArguments(), true);
				return IssueList.newInstance(arg);
			}

			@Override
			public CharSequence getName() {
				return getActivity().getString(R.string.ticket_issue);
			}
		});

		// current user
		ProjectArgument arg = new ProjectArgument();
		arg.setArgument(getArguments());
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
				final int target_id = target.getId();

				list.add(new CorePager.PageFragment() {
					@Override
					public Fragment getFragment() {
						FilterArgument param = new FilterArgument();
						param.setArgument(getArguments(), true);
						param.setFilterId(target_id);
						return IssueList.newInstance(param);
					}

					@Override
					public CharSequence getName() {
						return user.getName();
					}
				});
			}
		} catch (SQLException e) {
			Log.e(TAG,"fetchCurrentUser", e);
		}

		// wiki
		list.add(new CorePager.PageFragment() {
			@Override
			public Fragment getFragment() {
				ProjectArgument arg = new ProjectArgument();
				arg.setArgument(getArguments(), true);
				return WikiList.newInstance(arg);
			}

			@Override
			public CharSequence getName() {
				return getActivity().getString(R.string.wiki);
			}
		});


		// version
		list.add(new CorePager.PageFragment() {
			@Override
			public Fragment getFragment() {
				ProjectArgument arg = new ProjectArgument();
				arg.setArgument(getArguments(), true);
				return VersionList.newInstance(arg);
			}

			@Override
			public CharSequence getName() {
				return getActivity().getString(R.string.ticket_version);
			}
		});


		// category
		list.add(new CorePager.PageFragment() {
			@Override
			public Fragment getFragment() {
				ProjectArgument arg = new ProjectArgument();
				arg.setArgument(getArguments(), true);
				return CategoryList.newInstance(arg);
			}

			@Override
			public CharSequence getName() {
				return getActivity().getString(R.string.ticket_category);
			}
		});
		ViewPager viewPager = (ViewPager) getView().findViewById(R.id.pager);
		viewPager.setAdapter(new CorePager(getChildFragmentManager(), list));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.pager_main, container, false);
	}

}
