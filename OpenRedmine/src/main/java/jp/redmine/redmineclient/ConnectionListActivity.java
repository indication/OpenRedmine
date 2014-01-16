package jp.redmine.redmineclient;

import jp.redmine.redmineclient.activity.handler.AttachmentActionHandler;
import jp.redmine.redmineclient.activity.handler.AttachmentActionInterface;
import jp.redmine.redmineclient.activity.handler.ConnectionActionInterface;
import jp.redmine.redmineclient.activity.handler.ConnectionListHandler;
import jp.redmine.redmineclient.activity.handler.Core.ActivityRegistry;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.activity.handler.IssueViewHandler;
import jp.redmine.redmineclient.activity.handler.TimeEntryHandler;
import jp.redmine.redmineclient.activity.handler.TimeentryActionInterface;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.fragment.ActivityInterface;
import jp.redmine.redmineclient.fragment.ConnectionList;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.j256.ormlite.android.apptools.OrmLiteFragmentActivity;

public class ConnectionListActivity extends OrmLiteFragmentActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	public ConnectionListActivity(){
		super();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ActivityHelper.setupTheme(this);
		super.onCreate(savedInstanceState);
		getSupportActionBar();

		/**
		 * Add fragment on first view only
		 * On rotate, this method would be called with savedInstanceState.
		 */
		if(savedInstanceState != null)
			return;
		getSupportFragmentManager().beginTransaction()
			.replace(android.R.id.content, ConnectionList.newInstance())
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
			public Intent getIntent(Class<?> activity) {
				return new Intent(getApplicationContext(),activity);
			}

			@Override
			public void kickActivity(Intent intent) {
				startActivity(intent);
			}

		};
		if(cls.equals(ConnectionActionInterface.class))
			return (T) new ConnectionListHandler(registry);
		if(cls.equals(WebviewActionInterface.class))
			return (T) new IssueViewHandler(registry);
		if(cls.equals(IssueActionInterface.class))
			return (T) new IssueViewHandler(registry);
		if(cls.equals(TimeentryActionInterface.class))
			return (T) new TimeEntryHandler(registry);
		if(cls.equals(AttachmentActionInterface.class))
			return (T) new AttachmentActionHandler(registry);
		return null;
	}
}
