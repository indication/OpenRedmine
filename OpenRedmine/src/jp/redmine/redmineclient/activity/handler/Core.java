package jp.redmine.redmineclient.activity.handler;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class Core {
	ActivityRegistry manager;
	public Core(ActivityRegistry manager){
		this.manager = manager;
	}

	protected void runTransaction(TransitFragment transcation, String stack){
		FragmentTransaction tran = manager.getFragment().beginTransaction();
		transcation.action(tran);
		tran.addToBackStack(stack);
		tran.commit();
	}

	public interface ActivityRegistry{
		public FragmentManager getFragment();
		public void kickActivity(Intent intent);
	}

	protected interface TransitFragment{
		public void action(FragmentTransaction tran);
	}
}
