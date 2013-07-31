package jp.redmine.redmineclient.activity.handler;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import jp.redmine.redmineclient.fragment.Empty;
import jp.redmine.redmineclient.fragment.IssueComment;
import jp.redmine.redmineclient.fragment.IssueEdit;
import jp.redmine.redmineclient.fragment.IssueTitle;
import jp.redmine.redmineclient.fragment.IssueView;
import jp.redmine.redmineclient.fragment.TimeEntryList;
import jp.redmine.redmineclient.param.IssueArgument;

public class IssueViewHandler extends Core
	implements IssueView.OnArticleSelectedListener, IntentAction {

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
		tran.replace(R.id.fragmentOneFooter, Empty.newInstance());
		tran.addToBackStack(null);
		tran.commit();
	}


	@Override
	public void onIssueSelected(int connectionid, int issueid) {
		IssueArgument arg = new IssueArgument();
		arg.setArgument();
		arg.setConnectionId(connectionid);
		arg.setIssueId(issueid);

		FragmentTransaction tran = manager.beginTransaction();
		tran.replace(R.id.fragmentOne, IssueView.newInstance(arg));
		tran.replace(R.id.fragmentOneHeader, IssueTitle.newInstance(arg));
		tran.replace(R.id.fragmentOneFooter, IssueComment.newInstance(arg));
		tran.addToBackStack(null);
		tran.commit();
	}

	@Override
	public void onIssueEdit(int connectionid, int issueid) {
		IssueArgument arg = new IssueArgument();
		arg.setArgument();
		arg.setConnectionId(connectionid);
		arg.setIssueId(issueid);

		FragmentTransaction tran = manager.beginTransaction();
		tran.replace(R.id.fragmentOne, IssueEdit.newInstance(arg));
		tran.replace(R.id.fragmentOneFooter, Empty.newInstance());
		tran.addToBackStack(null);
		tran.commit();
	}

	@Override
	public void onIssueAdd(int connectionid, long projectId) {
		IssueArgument arg = new IssueArgument();
		arg.setArgument();
		arg.setConnectionId(connectionid);
		arg.setProjectId(projectId);
		arg.setIssueId(-1);

		FragmentTransaction tran = manager.beginTransaction();
		tran.replace(R.id.fragmentOne, IssueEdit.newInstance(arg));
		tran.addToBackStack(null);
		tran.commit();
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

	@Override
	public void issue(int connection, int issueid) {
		onIssueSelected(connection, issueid);
	}

	@Override
	public boolean url(String url) {

		return false;
	}

}
