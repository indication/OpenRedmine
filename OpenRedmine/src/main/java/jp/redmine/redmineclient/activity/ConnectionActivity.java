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
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.fragment.IssueList;
import jp.redmine.redmineclient.fragment.ProjectFavoriteList;
import jp.redmine.redmineclient.fragment.ProjectList;
import jp.redmine.redmineclient.fragment.RecentIssueList;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.param.FilterArgument;

public class ConnectionActivity extends TabActivity<DatabaseCacheHelper> {
	private static final String TAG = ConnectionActivity.class.getSimpleName();
	public ConnectionActivity(){
		super();
	}
	@Override
	protected List<CorePage> getTabs(){

		ConnectionArgument intent = new ConnectionArgument();
		intent.setIntent(getIntent());

		// setup navigation
		RedmineConnection con = ConnectionModel.getItem(getApplicationContext(), intent.getConnectionId());
		if(con.getId() != null)
			setTitle(con.getName());

		List<CorePage> list = new ArrayList<CorePage>();
		// Project list
		ConnectionArgument argList = new ConnectionArgument();
		argList.setArgument();
		argList.importArgument(intent);
		list.add((new CorePage<ConnectionArgument>() {
					@Override
					public Fragment getRawFragment(ConnectionArgument param) {
						return ProjectList.newInstance(param);
					}
				})
				.setParam(argList)
				.setName(getString(R.string.ticket_project))
				.setIcon(R.drawable.ic_project)
		);

		RedmineUserModel mUserModel = new RedmineUserModel(getHelper());
		try {
			RedmineUser user = mUserModel.fetchCurrentUser(intent.getConnectionId());
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

				if(target != null) {
					FilterArgument argIssue = new FilterArgument();
					argIssue.setArgument();
					argIssue.importArgument(intent);
					argIssue.setFilterId(target.getId());
					list.add((new CorePage<FilterArgument>() {
								@Override
								public Fragment getRawFragment(FilterArgument param) {
									return IssueList.newInstance(param);
								}
							})
									.setParam(argIssue)
									.setName(user.getName())
									.setIcon(R.drawable.ic_action_user)
					);
				}
			}
		} catch (SQLException e) {
			Log.e(TAG,"fetchCurrentUser", e);
		}

		ConnectionArgument argFavorite = new ConnectionArgument();
		argFavorite.setArgument();
		argFavorite.importArgument(intent);
		list.add((new CorePage<ConnectionArgument>() {
					@Override
					public Fragment getRawFragment(ConnectionArgument param) {
						return ProjectFavoriteList.newInstance(param);
					}
				})
						.setParam(argFavorite)
						.setName(getString(R.string.favorite))
						.setIcon(R.drawable.ic_project_favorite)
		);

		ConnectionArgument argRecent = new ConnectionArgument();
		argRecent.setArgument();
		argRecent.importArgument(intent);
		list.add((new CorePage<ConnectionArgument>() {
					@Override
					public Fragment getRawFragment(ConnectionArgument param) {
						return RecentIssueList.newInstance(param);
					}
				})
						.setParam(argRecent)
						.setName(getString(R.string.recent_issues))
						.setIcon(R.drawable.ic_recent)
		);

		return list;
	}


}
