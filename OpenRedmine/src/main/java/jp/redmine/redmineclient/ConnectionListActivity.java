package jp.redmine.redmineclient;

import jp.redmine.redmineclient.activity.TabActivity;
import jp.redmine.redmineclient.activity.pager.CorePage;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.fragment.ActivityInterface;
import jp.redmine.redmineclient.fragment.ConnectionList;
import jp.redmine.redmineclient.fragment.ProjectFavoriteList;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class ConnectionListActivity extends TabActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	public ConnectionListActivity(){
		super();
	}
	@Override
	protected List<CorePage> getTabs(){

		List<CorePage> list = new ArrayList<CorePage>();
		list.add((new CorePage<Void>() {
			@Override
			public Fragment getRawFragment() {
				return ConnectionList.newInstance();
			}

			@Override
			public CharSequence getName() {
				return getString(R.string.connection);
			}

			@Override
			public Integer getIcon() {
				return android.R.drawable.ic_menu_mapmode;
			}
		}).setParam(null));

		list.add((new CorePage<Void>() {
			@Override
			public Fragment getRawFragment() {
				return ProjectFavoriteList.newInstance();
			}

			@Override
			public CharSequence getName() {
				return getString(R.string.favorite);
			}

			@Override
			public Integer getIcon() {
				return android.R.drawable.btn_star;
			}
		}).setParam(null));
		return list;
	}
}
