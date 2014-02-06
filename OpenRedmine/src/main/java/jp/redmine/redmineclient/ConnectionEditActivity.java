package jp.redmine.redmineclient;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.j256.ormlite.android.apptools.OrmLiteFragmentActivity;

import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.fragment.ConnectionEdit;
import jp.redmine.redmineclient.param.ConnectionArgument;

public class ConnectionEditActivity extends OrmLiteFragmentActivity<DatabaseCacheHelper> {
	private static final String TAG = ConnectionEditActivity.class.getSimpleName();
	public ConnectionEditActivity(){
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ActivityHelper.setupTheme(this);
		super.onCreate(savedInstanceState);
		getSupportActionBar();

		ConnectionArgument intent = new ConnectionArgument();
		intent.setIntent(getIntent());

		ConnectionArgument arg = new ConnectionArgument();
		arg.setArgument();
		arg.importArgument(intent);
		FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
		tran.replace(android.R.id.content, ConnectionEdit.newInstance(arg));
		tran.commit();

	}
}
