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
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.fragment.IssueJump;
import jp.redmine.redmineclient.fragment.IssueList;
import jp.redmine.redmineclient.fragment.ProjectList;
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

		ConnectionArgument arg = new ConnectionArgument();
		arg.setArgument();
		arg.setConnectionId(intent.getConnectionId());

		// setup navigation
		ConnectionModel mConnection = new ConnectionModel(getApplicationContext());
		RedmineConnection con = mConnection.getItem(intent.getConnectionId());
		if(con.getId() != null)
			setTitle(con.getName());

		List<CorePage> list = new ArrayList<CorePage>();
		// Project list
		ConnectionArgument argList = new ConnectionArgument();
		argList.setArgument(arg.getArgument(), true);
		list.add((new CorePage<ConnectionArgument>() {
			@Override
			public Fragment getRawFragment() {
				return ProjectList.newInstance(getParam());
			}

			@Override
			public CharSequence getName() {
				return getString(R.string.ticket_project);
			}

			@Override
			public Integer getIcon() {
				return android.R.drawable.ic_menu_mapmode;
			}
		}).setParam(argList));

		// Direct issue jump list
		ConnectionArgument argJump = new ConnectionArgument();
		argJump.setArgument(arg.getArgument(), true);
		list.add((new CorePage<ConnectionArgument>() {
			@Override
			public Fragment getRawFragment() {
				return IssueJump.newInstance(getParam());
			}

			@Override
			public CharSequence getName() {
				return getString(R.string.ticket_jump);
			}

			@Override
			public Integer getIcon() {
				return android.R.drawable.ic_menu_directions;
			}
		}).setParam(argJump));



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
				argIssue.setArgument(arg.getArgument(), true);
				argIssue.setFilterId(target.getId());
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
				}).setParam(argIssue));
			}
		} catch (SQLException e) {
			Log.e(TAG,"fetchCurrentUser", e);
		}

		return list;
	}


}
