package jp.redmine.redmineclient.activity.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class CorePager extends FragmentStatePagerAdapter {
	private final List<CorePage> fragmentlist;

	public CorePager(FragmentManager fm, List<CorePage> fragments) {
		super(fm);
		fragmentlist = fragments;
	}

	@Override
	public Fragment getItem(int index) {
		if(fragmentlist.size() <= index)
			return null;
		return fragmentlist.get(index).getFragment();
	}

	@Override
	public int getCount() {
		return fragmentlist.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if(fragmentlist.size() <= position)
			return null;
		return fragmentlist.get(position).getName();
	}
}
