package jp.redmine.redmineclient;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.model.ConnectionModel;
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		Handler hdl = new Handler();
		hdl.postDelayed(new Runnable() {

			private boolean hasList(){
				ConnectionModel modelConnection = new ConnectionModel(getApplicationContext());
				int count = 0;
				for(@SuppressWarnings("unused") RedmineConnection con : modelConnection.fetchAllData()){
					count++;
				}
				return count>0;
			}

			public void run() {
				//@todo: first time.
				Intent i = new Intent(getApplication(), hasList() ? ConnectionListActivity.class : ConnectionActivity.class);
				startActivity(i);
				SplashActivity.this.finish();
			}
		}, 500);

	}
}
