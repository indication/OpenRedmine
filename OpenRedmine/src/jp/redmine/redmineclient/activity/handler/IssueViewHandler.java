package jp.redmine.redmineclient.activity.handler;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.fragment.IssueTitle;
import jp.redmine.redmineclient.fragment.IssueView;
import jp.redmine.redmineclient.fragment.TimeEntryList;
import jp.redmine.redmineclient.param.IssueArgument;

public class IssueViewHandler extends Core
	implements IssueView.OnArticleSelectedListener {

	public IssueViewHandler(FragmentManager manager) {
		super(manager);
	}


	@Override
	public void onTimeEntrySelected(int connectionid, int issueid) {
		IssueArgument arg = new IssueArgument();
		arg.setArgument();
		arg.setConnectionId(connectionid);
		arg.setIssueId(issueid);

		FragmentTransaction tran = manager.beginTransaction();
		tran.replace(R.id.fragmentOne, TimeEntryList.newInstance(arg));
		tran.addToBackStack(null);
		tran.commit();
	}

	@Override
	public void onIssueEdit(int connectionid, int issueid) {
	}

	@Override
	public void onIssueRefreshed(int connectionid, int issueid) {
		IssueArgument arg = new IssueArgument();
		arg.setArgument();
		arg.setConnectionId(connectionid);
		arg.setIssueId(issueid);

		FragmentTransaction tran = manager.beginTransaction();
		tran.replace(R.id.fragmentOneHeader, IssueTitle.newInstance(arg));
		tran.addToBackStack(null);
		tran.commit();
		manager.popBackStack();
	}

}
