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

	protected void kickActivity(Class<?> activity, IntentFactory factory){
		Intent intent = manager.getIntent(activity);
		factory.generateIntent(intent);
		manager.kickActivity(intent);
	}

	public interface ActivityRegistry{
		public FragmentManager getFragment();
		public Intent getIntent(Class<?> activity);
		public void kickActivity(Intent intent);
	}
	public interface IntentFactory{
		public void generateIntent(Intent intent);
	}

	protected interface TransitFragment{
		public void action(FragmentTransaction tran);
	}
}
