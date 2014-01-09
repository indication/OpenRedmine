package jp.redmine.redmineclient.activity.handler;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;

import jp.redmine.redmineclient.ConnectionActivity;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.fragment.ConnectionEdit;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.param.ProjectArgument;

public class ConnectionListHandler extends Core implements ConnectionActionInterface {

	public ConnectionListHandler(ActivityRegistry manager) {
		super(manager);
	}

	@Override
	public void onConnectionSelected(final int connectionid) {
		kickActivity(ConnectionActivity.class, new IntentFactory() {
			@Override
			public void generateIntent(Intent intent) {
				ProjectArgument arg = new ProjectArgument();
				arg.setIntent(intent);
				arg.setConnectionId(connectionid);
			}
		});
	}

	@Override
	public void onConnectionEdit(int connectionid) {

		final ConnectionArgument arg = new ConnectionArgument();
		arg.setArgument();
		arg.setConnectionId(connectionid);

		runTransaction(new TransitFragment() {
			@Override
			public void action(FragmentTransaction tran) {
				tran.replace(android.R.id.content, ConnectionEdit.newInstance(arg));
			}
		}, null);

	}

	@Override
	public void onConnectionAdd() {
		onConnectionEdit(-1);
	}

}
