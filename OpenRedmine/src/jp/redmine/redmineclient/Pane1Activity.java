package jp.redmine.redmineclient;

import com.j256.ormlite.android.apptools.OrmLiteFragmentActivity;

import jp.redmine.redmineclient.activity.handler.ConnectionListHandler;
import jp.redmine.redmineclient.activity.handler.Core.ActivityRegistry;
import jp.redmine.redmineclient.activity.handler.IssueViewHandler;
import jp.redmine.redmineclient.activity.handler.TimeEntryHandler;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import jp.redmine.redmineclient.fragment.ActivityInterface;
import jp.redmine.redmineclient.fragment.ConnectionList;
import jp.redmine.redmineclient.fragment.IssueView;
import jp.redmine.redmineclient.fragment.TimeEntryList;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class Pane1Activity extends OrmLiteFragmentActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	public Pane1Activity(){
		super();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.fragment_one);

		/**
		 * Add fragment on first view only
		 * On rotate, this method would be called with savedInstanceState.
		 */
		if(savedInstanceState != null)
			return;
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragmentOne, ConnectionList.newInstance())
			.commit();
	}

	@SuppressWarnings("unchecked")
	public <T> T getHandler(Class<T> cls){
		ActivityRegistry registry = new ActivityRegistry(){

			@Override
			public FragmentManager getFragment() {
				return getSupportFragmentManager();
			}

			@Override
			public void kickActivity(Intent intent) {
				startActivity(intent);
			}

		};
		if(cls.equals(ConnectionList.OnArticleSelectedListener.class))
			return (T) new ConnectionListHandler(registry);
		if(cls.equals(IntentAction.class))
			return (T) new IssueViewHandler(registry);
		if(cls.equals(IssueView.OnArticleSelectedListener.class))
			return (T) new IssueViewHandler(registry);
		if(cls.equals(TimeEntryList.OnArticleSelectedListener.class))
			return (T) new TimeEntryHandler(registry);
		return null;
	}
}
