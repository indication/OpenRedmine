package jp.redmine.redmineclient.activity.handler;

import android.content.Intent;

import jp.redmine.redmineclient.activity.ConnectionActivity;
import jp.redmine.redmineclient.activity.ConnectionEditActivity;
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
	public void onConnectionEdit(final int connectionid) {
		kickActivity(ConnectionEditActivity.class, new IntentFactory() {
			@Override
			public void generateIntent(Intent intent) {
				ConnectionArgument arg = new ConnectionArgument();
				arg.setIntent(intent);
				arg.setConnectionId(connectionid);
			}
		});

	}

	@Override
	public void onConnectionAdd() {
		onConnectionEdit(-1);
	}

	@Override
	public void onConnectionSaved() {

	}

}
