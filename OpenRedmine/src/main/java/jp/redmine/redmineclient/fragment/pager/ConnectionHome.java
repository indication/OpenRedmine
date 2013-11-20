package jp.redmine.redmineclient.fragment.pager;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.util.LinkedList;
import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.fragment.IssueJump;
import jp.redmine.redmineclient.fragment.ProjectList;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.param.ProjectArgument;

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
		List<CorePager.PageFragment> list = new LinkedList<CorePager.PageFragment>();
		// Project list
		list.add(new CorePager.PageFragment() {
			@Override
			public Fragment getFragment() {
				ConnectionArgument arg = new ConnectionArgument();
				arg.setArgument(getArguments());
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
				arg.setArgument(getArguments());
				return IssueJump.newInstance(arg);
			}

			@Override
			public CharSequence getName() {
				return getActivity().getString(R.string.ticket_jump);
			}
		});

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
