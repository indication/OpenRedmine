package jp.redmine.redmineclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.activity.TabActivity;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.activity.pager.CorePage;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.fragment.ActivityInterface;
import jp.redmine.redmineclient.fragment.IssueEdit;
import jp.redmine.redmineclient.fragment.IssueView;
import jp.redmine.redmineclient.fragment.TimeEntryEdit;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.param.TimeEntryArgument;

public class IssueActivity extends TabActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	private static final String TAG = IssueActivity.class.getSimpleName();
	public IssueActivity(){
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected List<CorePage> getTabs(){

		IssueArgument intent = new IssueArgument();
		intent.setIntent(getIntent());

		// setup navigation
		try {
			RedmineProject proj = null;
			if(intent.getProjectId() < 0){
				RedmineIssueModel mIssue = new RedmineIssueModel(getHelper());
				RedmineIssue issue = mIssue.fetchById(intent.getConnectionId(), intent.getIssueId());
				proj = issue.getProject();
			}
			if(proj == null){
				RedmineProjectModel mProject = new RedmineProjectModel(getHelper());
				proj = mProject.fetchById(intent.getProjectId());
			}
			if(proj != null && proj.getId() != null){
				setTitle(proj.getName());
			}
		} catch (SQLException e) {
			Log.e(TAG, "getTabs", e);
		}

		boolean isValidIssue = intent.getIssueId() > 0;

		List<CorePage> list = new ArrayList<CorePage>();
		if(isValidIssue){
			// Issue view
			IssueArgument argList = new IssueArgument();
			argList.setArgument();
			argList.importArgument(intent);
			list.add((new CorePage<IssueArgument>() {
				@Override
				public Fragment getRawFragment() {
					return IssueView.newInstance(getParam());
				}

				@Override
				public CharSequence getName() {
					return getString(R.string.ticket_issue);
				}

				@Override
				public Integer getIcon() {
					return R.drawable.ic_action_message;
				}
			}).setParam(argList));

			// Time Entry

			TimeEntryArgument argTimeentry = new TimeEntryArgument();
			argTimeentry.setArgument();
			argTimeentry.importArgument(intent);
			list.add((new CorePage<TimeEntryArgument>() {
				@Override
				public Fragment getRawFragment() {
					return TimeEntryEdit.newInstance(getParam());
				}

				@Override
				public CharSequence getName() {
					return getString(R.string.ticket_time);
				}

				@Override
				public Integer getIcon() {
					return android.R.drawable.ic_menu_recent_history;
				}
			}).setParam(argTimeentry));
		}

		IssueArgument argEdit = new IssueArgument();
		argEdit.setArgument();
		argEdit.importArgument(intent);
		list.add((new CorePage<IssueArgument>() {
			@Override
			public Fragment getRawFragment() {
				return IssueEdit.newInstance(getParam());
			}

			@Override
			public CharSequence getName() {
				return getString(R.string.edit);
			}

			@Override
			public Integer getIcon() {
				return android.R.drawable.ic_menu_edit;
			}
		}).setParam(argEdit));


		return list;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:

				IssueArgument intent = new IssueArgument();
				intent.setIntent(getIntent());
				if(intent.getProjectId() < 0){
					RedmineProject proj = null;
					try {
						RedmineIssueModel mIssue = new RedmineIssueModel(getHelper());
						RedmineIssue issue = mIssue.fetchById(intent.getConnectionId(), intent.getIssueId());
						proj = issue.getProject();
					} catch (SQLException e) {
						Log.e(TAG, "onOptionsItemSelected", e);
					}
					if(proj != null && proj.getId() != null){
						IssueActionInterface handler = getHandler(IssueActionInterface.class);
						handler.onIssueList(intent.getConnectionId(),  proj.getId());
						finish();
					}
				} else {
					finish();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
