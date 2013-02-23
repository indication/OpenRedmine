package jp.redmine.redmineclient.parser;

import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineCategoryModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineJournalModel;
import jp.redmine.redmineclient.db.cache.RedminePriorityModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineJournal;

public class IssueModelDataCreationHandler implements DataCreationHandler<RedmineConnection,RedmineIssue> {

	private RedmineIssueModel mIssue;
	private RedmineVersionModel mVersion;
	private RedmineUserModel mUser;
	private RedmineTrackerModel mTracker;
	private RedmineStatusModel mStatus;
	private RedminePriorityModel mPriority;
	private RedmineCategoryModel mCategory;
	private RedmineJournalModel mJournal;
	private RedmineProjectModel mProject;
	public IssueModelDataCreationHandler(DatabaseCacheHelper helperCache){
		mIssue = new RedmineIssueModel(helperCache);
		mVersion = new RedmineVersionModel(helperCache);
		mUser = new RedmineUserModel(helperCache);
		mTracker = new RedmineTrackerModel(helperCache);
		mStatus = new RedmineStatusModel(helperCache);
		mCategory = new RedmineCategoryModel(helperCache);
		mPriority = new RedminePriorityModel(helperCache);
		mJournal = new RedmineJournalModel(helperCache);
		mProject = new RedmineProjectModel(helperCache);
	}
	public void onData(RedmineConnection connection,RedmineIssue data) throws SQLException {
		data.setConnectionId(connection.getId());
		mProject.refreshItem(connection, data);
		RedmineIssue.setupConnectionId(data);
		RedmineIssue.setupProjectId(data);
		mTracker.refreshItem(data);
		mVersion.refreshItem(data);
		mUser.refreshItem(data);
		mStatus.refreshItem(data);
		mPriority.refreshItem(data);
		mCategory.refreshItem(data);
		mIssue.refreshItem(connection,data);
		RedmineIssue.setupJournals(data);
		onDataJournal(data);
	}
	public void onDataJournal(RedmineIssue data) throws SQLException {
		if(data.getJournals() == null)
			return;
		for (RedmineJournal journal : data.getJournals()){
			mUser.refreshItem(journal);
			mJournal.refreshItem(journal);
		}
	}
}
