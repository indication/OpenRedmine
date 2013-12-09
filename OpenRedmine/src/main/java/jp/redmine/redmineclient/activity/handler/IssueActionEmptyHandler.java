package jp.redmine.redmineclient.activity.handler;

public class IssueActionEmptyHandler implements IssueActionInterface {
	@Override
	public void onIssueFilterList(int connectionId, int filterid) {}
	@Override
	public void onIssueList(int connectionId, long projectId) {}
	@Override
	public void onIssueSelected(int connectionid, int issueid) {}
	@Override
	public void onIssueEdit(int connectionid, int issueid) {}
	@Override
	public void onIssueRefreshed(int connectionid, int issueid) {}
	@Override
	public void onIssueAdd(int connectionId, long projectId) {}
}