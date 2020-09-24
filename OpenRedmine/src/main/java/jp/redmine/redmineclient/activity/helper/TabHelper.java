package jp.redmine.redmineclient.activity.helper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;

public class TabHelper implements ActionBar.TabListener{
	protected Fragment mFragment;
	protected int mResource;
	public TabHelper(Fragment frag, int target_layout){
		mFragment = frag;
		mResource = target_layout;
	}
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		fragmentTransaction.replace(mResource, mFragment);
		fragmentTransaction.attach(mFragment);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		fragmentTransaction.detach(mFragment);
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}
}
