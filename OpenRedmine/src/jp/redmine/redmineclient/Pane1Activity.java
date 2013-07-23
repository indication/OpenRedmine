package jp.redmine.redmineclient;

import com.j256.ormlite.android.apptools.OrmLiteFragmentActivity;

import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.fragment.ConnectionList;
import android.os.Bundle;

public class Pane1Activity extends OrmLiteFragmentActivity<DatabaseCacheHelper>  {
	public Pane1Activity(){
		super();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.fragment_one);


		getSupportFragmentManager().beginTransaction()
			.add(R.id.fragmentOne, ConnectionList.newInstance())
			.commit();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
}
