package jp.redmine.redmineclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.LinearLayout;

import com.j256.ormlite.android.apptools.OrmLiteFragmentActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.form.helper.ViewIdGenerator;
import jp.redmine.redmineclient.fragment.ActivityInterface;
import jp.redmine.redmineclient.fragment.IssueList;
import jp.redmine.redmineclient.param.FilterArgument;
import jp.redmine.redmineclient.param.ProjectArgument;

public class KanbanActivity extends OrmLiteFragmentActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	private static final String TAG = KanbanActivity.class.getSimpleName();
	public KanbanActivity(){
		super();
	}
	private ViewIdGenerator generator = new ViewIdGenerator();
	private static final String keyListFragments = "KanbanListFragments";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ActivityHelper.setupTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_multiview);
		getSupportActionBar();

		/**
		 * Add fragment on first view only
		 * On rotate, this method would be called with savedInstanceState.
		 */
		if(savedInstanceState != null)
			return;

		renewFragments(savedInstanceState);
	}

	protected void renewFragments(Bundle savedInstanceState){
		FragmentManager manager =  getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		LinearLayout layout = (LinearLayout)findViewById(R.id.list);

		ProjectArgument arg = new ProjectArgument();
		arg.setIntent(getIntent());
		RedmineStatusModel mStatus = new RedmineStatusModel(getHelper());
		RedmineFilterModel mFilter = new RedmineFilterModel(getHelper());
		//create project
		RedmineProject itemProject = new RedmineProject();
		itemProject.setConnectionId(arg.getConnectionId());
		itemProject.setId(arg.getProjectId());
		try {
			for (RedmineStatus item : mStatus.fetchAll(arg.getConnectionId())) {
				//setup parameter
				RedmineFilter filter = new RedmineFilter();
				filter.setConnectionId(item.getConnectionId());
				filter.setStatus(item);
				filter.setProject(itemProject);
				filter.setSort(RedmineFilterSortItem.getFilter(RedmineFilterSortItem.KEY_MODIFIED, false));
				RedmineFilter target = mFilter.getSynonym(filter);
				if (target == null) {
					mFilter.insert(filter);
					target = filter;
				}

				FilterArgument argFilter = new FilterArgument();
				argFilter.setArgument();
				argFilter.importArgument(arg);
				argFilter.setFilterId(target.getId());

				String keyLayout = keyListFragments + target.getId();
				Integer idLayout = savedInstanceState.getInt(keyLayout, -1);
				if (idLayout == -1) {
					idLayout = generator.getViewId(layout);
					LinearLayout addlayout = new LinearLayout(this);
					addlayout.setId(idLayout);
					layout.addView(addlayout);
					transaction.add(idLayout, IssueList.newInstance(argFilter));
					savedInstanceState.putInt(keyLayout, idLayout);
				} else {
					transaction.replace(idLayout, IssueList.newInstance(argFilter));
				}
			}
		} catch (SQLException e) {
			Log.e(TAG, "renewFragments", e);
		}
		transaction.commit();

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
