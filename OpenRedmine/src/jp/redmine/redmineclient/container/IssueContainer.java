package jp.redmine.redmineclient.container;

import java.io.Serializable;

import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.url.RemoteUrlIssues;

public class IssueContainer implements Serializable {
	private static final long serialVersionUID = 580944798932035277L;
	public RedmineUser authors;
	public RedmineUser creators;
	public RedmineUser assigned;
	public RedmineProject project;
	public RedmineTracker tracker;
	public RedminePriority priority;
	public RedmineProjectVersion version;
	public RedmineStatus status;
	public RedmineIssue issue;
	public DateContainer modify  = new DateContainer();
	public DateContainer created  = new DateContainer();
	public DateContainer imported  = new DateContainer();

	public void setupIssueUrl(RemoteUrlIssues url){
		if(assigned != null && assigned.getUserId() != null)
			url.filterAssigned(assigned.getUserId().toString());
		if(project != null && project.getProjectId() != null)
			url.filterProject(project.getProjectId().toString());
		if(status != null)
				url.filterStatus(status.getStatusId() == null ?
						"*" : status.getStatusId().toString());
		if(tracker != null && tracker.getTrackerId() != null)
			url.filterTracker(tracker.getTrackerId().toString());
	}

}
