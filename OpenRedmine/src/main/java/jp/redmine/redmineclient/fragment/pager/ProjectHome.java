package jp.redmine.redmineclient.fragment.pager;

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
import jp.redmine.redmineclient.fragment.IssueList;
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
		final ProjectArgument arg = new ProjectArgument();
		arg.setArgument(getArguments());
		List<CorePager.PageFragment> list = new LinkedList<CorePager.PageFragment>();
		// Project list
		list.add(new CorePager.PageFragment() {
			@Override
			public Fragment getFragment() {
				return IssueList.newInstance(arg);
			}

			@Override
			public CharSequence getName() {
				return getActivity().getString(R.string.ticket_issue);
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
