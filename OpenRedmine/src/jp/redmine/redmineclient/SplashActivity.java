package jp.redmine.redmineclient;

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
		setContentView(R.layout.splash);
		Handler hdl = new Handler();
		hdl.postDelayed(new Runnable() {

			public void run() {
				//@todo: first time.
				Intent i = new Intent(getApplication(), ConnectionListActivity.class);
				startActivity(i);
				SplashActivity.this.finish();
			}
		}, 200);

	}
}
