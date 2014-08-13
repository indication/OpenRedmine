package jp.redmine.redmineclient.activity;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity{
	public SplashActivity(){
		super();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityHelper.setupTheme(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.page_splash);
		Handler hdl = new Handler();
		hdl.postDelayed(new Runnable() {

			public void run() {
				Intent i = new Intent(getApplication(), ConnectionListActivity.class);
				startActivity(i);
				SplashActivity.this.finish();
			}
		}, 200);

	}
}
