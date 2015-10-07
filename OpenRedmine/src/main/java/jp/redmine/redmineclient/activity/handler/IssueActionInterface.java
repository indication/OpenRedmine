package jp.redmine.redmineclient.activity.handler;

public interface IssueActionInterface {
	public void onIssueFilterList(int connectionid, int filterid);
	public void onIssueList(int connectionid, long projectid);
	void onIssueList(int connectionid, long projectid, String kind, long id);
	public void onKanbanList(int connectionid, long projectid);
	public void onIssueSelected(int connectionid, int issueid);
	public void onIssueEdit(int connectionid, int issueid);
	public void onIssueRefreshed(int connectionid, int issueid);
	public void onIssueAdd(int connectionId, long projectId);
}