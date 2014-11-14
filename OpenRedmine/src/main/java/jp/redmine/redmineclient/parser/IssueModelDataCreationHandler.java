package jp.redmine.redmineclient.parser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineAttachmentModel;
import jp.redmine.redmineclient.db.cache.RedmineCategoryModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueRelationModel;
import jp.redmine.redmineclient.db.cache.RedmineJournalModel;
import jp.redmine.redmineclient.db.cache.RedminePriorityModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.db.cache.RedmineWatcherModel;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineIssueRelation;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedmineWatcher;

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
	private RedmineIssueRelationModel mRelation;
	private RedmineAttachmentModel mAttachment;
	private RedmineWatcherModel mWatcher;
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
		mRelation = new RedmineIssueRelationModel(helperCache);
		mAttachment = new RedmineAttachmentModel(helperCache);
		mWatcher = new RedmineWatcherModel(helperCache);
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
		RedmineIssue.setupRelations(data);
		onDataRelation(data);
		RedmineIssue.setupAttachments(data);
		onDataAttachment(data);
		RedmineIssue.setupWatchers(data);
		onDataWatchers(data);
	}
	public void onDataJournal(RedmineIssue data) throws SQLException {
		if(data.getJournals() == null)
			return;
		for (RedmineJournal journal : data.getJournals()){
			mUser.refreshItem(journal);
			mJournal.refreshItem(journal);
		}
	}
	public void onDataRelation(RedmineIssue data) throws SQLException {
		if(data.getRelations() == null)
			return;
		List<Integer> listRelations = new LinkedList<Integer>();
		for (RedmineIssueRelation journal : data.getRelations()){
			mRelation.refreshItem(journal);
			listRelations.add(journal.getRelationId());
		}
		for(RedmineIssueRelation relation : mRelation.fetchByIssue(data.getConnectionId(), data.getIssueId(), null, null)){
			if(!listRelations.contains(relation.getRelationId())){
				mRelation.delete(relation);
			}
		}
	}
	public void onDataAttachment(RedmineIssue data) throws SQLException {
		if(data.getAttachments() == null)
			return;
		for (RedmineAttachment attachment : data.getAttachments()){
			mUser.refreshItem(attachment);
			mAttachment.refreshItem(attachment);
		}
	}
	public void onDataWatchers(RedmineIssue data) throws SQLException {
		if(data.getWatchers() == null)
			return;
		List<Integer> listUsers = new ArrayList<Integer>();
		for (RedmineWatcher watcher : data.getWatchers()){
			mUser.refreshItem(watcher);
			mWatcher.refreshItem(watcher);
			if(watcher.getUser() != null)
				listUsers.add(watcher.getUser().getUserId());
		}
		for (RedmineWatcher watcher : mWatcher.fetchByIssue(data.getConnectionId(), data.getIssueId())){
			if(watcher.getUser() == null)
				continue;
			if(!listUsers.contains(watcher.getUser().getUserId())){
				mWatcher.delete(watcher);
			}
		}
	}
}
