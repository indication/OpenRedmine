package jp.redmine.redmineclient.activity.handler;

public interface IssueActionInterface {
	void onIssueFilterList(int connectionid, int filterid);
	void onIssueList(int connectionid, long projectid);
	void onKanbanList(int connectionid, long projectid);
	void onIssueSelected(int connectionid, int issueid);
	void onIssueEdit(int connectionid, int issueid);
	void onIssueRefreshed(int connectionid, int issueid);
	void onIssueAdd(int connectionId, long projectId);
}