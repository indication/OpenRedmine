package jp.redmine.redmineclient.activity.handler;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.fragment.ConnectionList;
import jp.redmine.redmineclient.fragment.ProjectList;
import jp.redmine.redmineclient.param.ConnectionArgument;

public class ConnectionListHandler extends Core implements ConnectionList.OnArticleSelectedListener {

	public ConnectionListHandler(FragmentManager manager) {
		super(manager);
	}

	@Override
	public void onConnectionSelected(int connectionid) {

		ConnectionArgument arg = new ConnectionArgument();
		arg.setArgument();
		arg.setConnectionId(connectionid);

		FragmentTransaction tran = manager.beginTransaction();
		ProjectList fragment = ProjectList.newInstance();
		fragment.setArguments(arg.getArgument());
		tran.replace(R.id.fragmentOne, fragment);
		tran.addToBackStack(null);
		tran.commit();
	}

	@Override
	public void onConnectionEdit(int connectionid) {

	}

	@Override
	public void onConnectionAdd() {

	}

}
