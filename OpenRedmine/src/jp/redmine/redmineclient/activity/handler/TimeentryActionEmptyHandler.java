package jp.redmine.redmineclient.activity.handler;

public class TimeentryActionEmptyHandler implements TimeentryActionInterface {

	@Override
	public void onTimeEntryList(int connectionid, int issueid) {}
	@Override
	public void onTimeEntrySelected(int connectionid, int issueid, int timeentryid) {}
	@Override
	public void onTimeEntryEdit(int connectionid, int issueid, int timeentryid) {}
	@Override
	public void onTimeEntryAdd(int connectionid, int issueid) {}
}