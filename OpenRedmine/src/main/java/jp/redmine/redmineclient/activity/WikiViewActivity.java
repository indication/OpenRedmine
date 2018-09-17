package jp.redmine.redmineclient.activity;

import android.annotation.TargetApi;
import android.app.FragmentBreadCrumbs;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import com.j256.ormlite.android.apptools.OrmLiteFragmentActivity;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.AttachmentActionHandler;
import jp.redmine.redmineclient.activity.handler.AttachmentActionInterface;
import jp.redmine.redmineclient.activity.handler.ConnectionActionInterface;
import jp.redmine.redmineclient.activity.handler.ConnectionListHandler;
import jp.redmine.redmineclient.activity.handler.Core;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.activity.handler.IssueViewHandler;
import jp.redmine.redmineclient.activity.handler.TimeEntryHandler;
import jp.redmine.redmineclient.activity.handler.TimeentryActionInterface;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.fragment.ActivityInterface;
import jp.redmine.redmineclient.fragment.WikiDetail;
import jp.redmine.redmineclient.param.WikiArgument;

public class WikiViewActivity extends OrmLiteFragmentActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	private static final String TAG = WikiViewActivity.class.getSimpleName();
	private FragmentBreadCrumbs mFragmentBreadCrumbs;
	public WikiViewActivity(){
		super();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ActivityHelper.setupTheme(this);
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		/*
		 * Add fragment on first view only
		 * On rotate, this method would be called with savedInstanceState.
		 */
		if(savedInstanceState != null)
			return;
		WikiArgument intent = new WikiArgument();
		intent.setIntent(getIntent());

		WikiArgument arg = new WikiArgument();
		arg.setArgument();
		arg.importArgument(intent);

		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, WikiDetail.newInstance(arg))
				.commit();
	}

	@SuppressWarnings("unchecked")
	public <T> T getHandler(Class<T> cls){
		Core.ActivityRegistry registry = new Core.ActivityRegistry(){

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
			return (T) new IssueViewHandler(registry){
				@Override
				public void wiki(int connection, long projectid, final String title) {
					final WikiArgument arg = new WikiArgument();
					arg.setArgument();
					arg.setConnectionId(connection);
					arg.setProjectId(projectid);
					arg.setWikiTitle(title);

					runTransaction(new TransitFragment() {
						@Override
						public void action(FragmentTransaction tran) {
							tran.add(android.R.id.content, WikiDetail.newInstance(arg));
							tran.setBreadCrumbTitle(title);
						}
					}, null);
				}
			};
		if(cls.equals(IssueActionInterface.class))
			return (T) new IssueViewHandler(registry);
		if(cls.equals(TimeentryActionInterface.class))
			return (T) new TimeEntryHandler(registry);
		if(cls.equals(AttachmentActionInterface.class))
			return (T) new AttachmentActionHandler(registry);
		return null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
