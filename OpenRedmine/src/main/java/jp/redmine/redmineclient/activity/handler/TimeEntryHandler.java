package jp.redmine.redmineclient.activity.handler;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.TimeEntryActivity;
import jp.redmine.redmineclient.fragment.TimeEntryList;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.param.TimeEntryArgument;

public class TimeEntryHandler extends Core implements TimeentryActionInterface {

	public TimeEntryHandler(ActivityRegistry manager) {
		super(manager);
	}


	@Override
	public void onTimeEntryList(int connectionid, int issueid) {
		final IssueArgument arg = new IssueArgument();
		arg.setArgument();
		arg.setConnectionId(connectionid);
		arg.setIssueId(issueid);

		runTransaction(new TransitFragment() {
			@Override
			public void action(FragmentTransaction tran) {
				tran.replace(R.id.fragmentOne, TimeEntryList.newInstance(arg));
				Fragment frag = manager.getFragment().findFragmentById(R.id.fragmentOneFooter);
				if(frag != null)
					tran.remove(frag);
			}
		}, null);

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
