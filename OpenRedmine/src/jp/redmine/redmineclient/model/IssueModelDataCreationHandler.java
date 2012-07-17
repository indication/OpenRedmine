package jp.redmine.redmineclient.model;

import java.sql.SQLException;

import android.util.Log;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineCategoryModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedminePriorityModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.external.DataCreationHandler;

public class IssueModelDataCreationHandler implements DataCreationHandler<RedmineProject,RedmineIssue> {

	private RedmineIssueModel mIssue;
	private RedmineVersionModel mVersion;
	private RedmineUserModel mUser;
	private RedmineTrackerModel mTracker;
	private RedmineStatusModel mStatus;
	private RedminePriorityModel mPriority;
	private RedmineCategoryModel mCategory;
	public IssueModelDataCreationHandler(DatabaseCacheHelper helperCache){
		mIssue = new RedmineIssueModel(helperCache);
		mVersion = new RedmineVersionModel(helperCache);
		mUser = new RedmineUserModel(helperCache);
		mTracker = new RedmineTrackerModel(helperCache);
		mStatus = new RedmineStatusModel(helperCache);
		mCategory = new RedmineCategoryModel(helperCache);
		mPriority = new RedminePriorityModel(helperCache);
	}
	public void onData(RedmineProject proj,RedmineIssue data) {
		Log.d("ParserIssue","OnData Called");
		try {
			data.setConnectionId(proj.getConnectionId());
			data.setProject(proj);
			RedmineIssue.setupConnectionId(data);
			mTracker.refreshItem(data);
			mVersion.refreshItem(data);
			mUser.refreshItem(data);
			mStatus.refreshItem(data);
			mPriority.refreshItem(data);
			mCategory.refreshItem(data);
			mIssue.refreshItem(proj,data);
		} catch (SQLException e) {
			Log.e("ParserIssue","onData",e);
		}
	}
}
