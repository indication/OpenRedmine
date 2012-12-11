package jp.redmine.redmineclient.parser;

import java.sql.SQLException;

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
	public void onData(RedmineProject proj,RedmineIssue data) throws SQLException {
		data.setConnectionId(proj.getConnectionId());
		data.setProject(proj);
		RedmineIssue.setupConnectionId(data);
		RedmineIssue.setupProjectId(data);
		mTracker.refreshItem(data);
		mVersion.refreshItem(data);
		mUser.refreshItem(data);
		mStatus.refreshItem(data);
		mPriority.refreshItem(data);
		mCategory.refreshItem(data);
		mIssue.refreshItem(proj,data);
	}
}
