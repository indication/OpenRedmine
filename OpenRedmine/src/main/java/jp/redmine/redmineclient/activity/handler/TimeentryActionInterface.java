package jp.redmine.redmineclient.activity.handler;

public interface TimeentryActionInterface {
	public void onTimeEntryList(int connectionid, int issueid);
	public void onTimeEntrySelected(int connectionid, int issueid, int timeentryid);
	public void onTimeEntryEdit(int connectionid, int issueid, Integer timeentryid);
	public void onTimeEntryAdd(int connectionid, int issueid);
}