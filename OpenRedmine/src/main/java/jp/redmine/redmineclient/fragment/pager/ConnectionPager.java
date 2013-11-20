package jp.redmine.redmineclient.fragment.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.fragment.IssueJump;
import jp.redmine.redmineclient.fragment.ProjectList;
import jp.redmine.redmineclient.param.ProjectArgument;

public class ConnectionPager extends FragmentStatePagerAdapter {
	private final ProjectArgument arg;

	public ConnectionPager(FragmentManager fm, ProjectArgument argument) {
		super(fm);
		arg = argument;
	}

	@Override
	public Fragment getItem(int index) {
		switch(index){
			case 0:		return ProjectList.newInstance(arg);
			case 1:		return IssueJump.newInstance(arg);
			default:	return null;
		}
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch(position){
			case 0:		return "All Projects";
			case 1:		return "Jump";
			//case 2:		return "Current User";
			default:	return null;
		}
	}
}
