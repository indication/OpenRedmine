package jp.redmine.redmineclient.fragment.pager;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.fragment.IssueJump;
import jp.redmine.redmineclient.fragment.IssueList;
import jp.redmine.redmineclient.fragment.ProjectList;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.param.FilterArgument;

public class ConnectionHome extends OrmLiteFragment<DatabaseCacheHelper> {
	private static final String TAG = ConnectionHome.class.getSimpleName();

	public ConnectionHome() {
		super();
	}

	static public ConnectionHome newInstance(ConnectionArgument intent) {
		ConnectionHome instance = new ConnectionHome();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		List<CorePager.PageFragment> list = new ArrayList<CorePager.PageFragment>();
		// Project list
		list.add(new CorePager.PageFragment() {
			@Override
			public Fragment getFragment() {
				ConnectionArgument arg = new ConnectionArgument();
				arg.setArgument(getArguments(), true);
				return ProjectList.newInstance(arg);
			}

			@Override
			public CharSequence getName() {
				return getActivity().getString(R.string.ticket_project);
			}
		});
		// Direct issue jump list
		list.add(new CorePager.PageFragment() {
			@Override
			public Fragment getFragment() {
				ConnectionArgument arg = new ConnectionArgument();
				arg.setArgument(getArguments(), true);
				return IssueJump.newInstance(arg);
			}

			@Override
			public CharSequence getName() {
				return getActivity().getString(R.string.ticket_jump);
			}
		});


		ConnectionArgument arg = new ConnectionArgument();
		arg.setArgument(getArguments());
		RedmineUserModel mUserModel = new RedmineUserModel(getHelper());
		try {
			final RedmineUser user = mUserModel.fetchCurrentUser(arg.getConnectionId());
			if(user != null){
				RedmineFilter filter = new RedmineFilter();
				filter.setConnectionId(arg.getConnectionId());
				filter.setAssigned(user);
				filter.setSort(RedmineFilterSortItem.getFilter(RedmineFilterSortItem.KEY_MODIFIED, false));
				RedmineFilterModel mFilter = new RedmineFilterModel(getHelper());
				RedmineFilter target = mFilter.getSynonym(filter);
				if (target == null) {
					mFilter.insert(filter);
					target = mFilter.getSynonym(filter);
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

		ViewPager viewPager = (ViewPager) getView().findViewById(R.id.pager);
		viewPager.setAdapter(new CorePager(getChildFragmentManager(), list));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.pager_main, container, false);
	}

	/*
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ActionBar actionBar = getActivity().getActionBar();
		if (actionBar == null)
			return;
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	}
	*/
}
