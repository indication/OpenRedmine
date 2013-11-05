package jp.redmine.redmineclient.activity.handler;

import android.support.v4.app.FragmentTransaction;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.fragment.ConnectionEdit;
import jp.redmine.redmineclient.fragment.Empty;
import jp.redmine.redmineclient.fragment.IssueJump;
import jp.redmine.redmineclient.fragment.ProjectList;
import jp.redmine.redmineclient.param.ConnectionArgument;

public class ConnectionListHandler extends Core implements ConnectionActionInterface {

	public ConnectionListHandler(ActivityRegistry manager) {
		super(manager);
	}

	@Override
	public void onConnectionSelected(int connectionid) {

		final ConnectionArgument arg = new ConnectionArgument();
		arg.setArgument();
		arg.setConnectionId(connectionid);

		runTransaction(new TransitFragment() {
			@Override
			public void action(FragmentTransaction tran) {
				tran.replace(R.id.fragmentOne, ProjectList.newInstance(arg));
				tran.replace(R.id.fragmentOneHeader, IssueJump.newInstance(arg));
			}
		}, null);
	}

	@Override
	public void onConnectionEdit(int connectionid) {

		final ConnectionArgument arg = new ConnectionArgument();
		arg.setArgument();
		arg.setConnectionId(connectionid);

		runTransaction(new TransitFragment() {
			@Override
			public void action(FragmentTransaction tran) {
				tran.replace(R.id.fragmentOne, ConnectionEdit.newInstance(arg));
				tran.replace(R.id.fragmentOneHeader, Empty.newInstance());
			}
		}, null);

	}

	@Override
	public void onConnectionAdd() {
		onConnectionEdit(-1);
	}

}
