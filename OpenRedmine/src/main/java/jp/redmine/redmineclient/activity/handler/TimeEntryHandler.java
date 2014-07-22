package jp.redmine.redmineclient.activity.handler;

import android.content.Intent;

import jp.redmine.redmineclient.activity.TimeEntryActivity;
import jp.redmine.redmineclient.param.TimeEntryArgument;

public class TimeEntryHandler extends Core implements TimeentryActionInterface {

	public TimeEntryHandler(ActivityRegistry manager) {
		super(manager);
	}


	@Override
	public void onTimeEntryList(int connectionid, int issueid) {
		throw new UnsupportedOperationException();
	}


	@Override
	public void onTimeEntrySelected(int connectionid, int issueid, int timeentryid) {
		onTimeEntryEdit(connectionid, issueid, timeentryid);
	}


	@Override
	public void onTimeEntryEdit(final int connectionid, final int issueid, final Integer timeentryid) {
		kickActivity(TimeEntryActivity.class, new IntentFactory() {
			@Override
			public void generateIntent(Intent intent) {
				TimeEntryArgument arg = new TimeEntryArgument();
				arg.setIntent(intent);
				arg.setConnectionId(connectionid);
				arg.setIssueId(issueid);
				if(timeentryid != null)
					arg.setTimeEntryId(timeentryid);
			}
		});
	}


	@Override
	public void onTimeEntryAdd(int connectionid, int issueid) {
		onTimeEntryEdit(connectionid, issueid, null);
	}

}
