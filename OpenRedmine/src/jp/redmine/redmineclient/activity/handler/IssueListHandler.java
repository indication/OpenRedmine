package jp.redmine.redmineclient.activity.handler;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import jp.redmine.redmineclient.fragment.IssueList;
import jp.redmine.redmineclient.fragment.IssueView;
import jp.redmine.redmineclient.param.IssueArgument;

public class IssueListHandler extends Core
	implements IssueList.OnArticleSelectedListener, IntentAction {

	public IssueListHandler(FragmentManager manager) {
		super(manager);
	}

	@Override
	public void onIssueSelected(int connectionid, long projectid, int issueid) {
		IssueArgument arg = new IssueArgument();
		arg.setArgument();
		arg.setConnectionId(connectionid);
		arg.setProjectId(projectid);
		arg.setIssueId(issueid);

		FragmentTransaction tran = manager.beginTransaction();
		tran.replace(R.id.fragmentOne, IssueView.newInstance(arg));
		tran.addToBackStack(null);
		tran.commit();
	}


	@Override
	public void onIssueEdit(int connectionid, long projectid, int issueid) {

	}


	@Override
	public void onIssueAdd(int connectionid, long projectid) {

	}

	@Override
	public void issue(int connection, int issueid) {
		onIssueSelected(connection, -1, issueid);
	}

	@Override
	public boolean url(String url) {

		return false;
	}

}
