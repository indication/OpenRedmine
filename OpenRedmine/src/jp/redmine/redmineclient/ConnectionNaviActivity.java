package jp.redmine.redmineclient;

import jp.redmine.redmineclient.form.RedmineNavigationForm;
import jp.redmine.redmineclient.intent.ConnectionNaviResultIntent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ConnectionNaviActivity extends Activity {
	public ConnectionNaviActivity(){
		super();
	}
	private RedmineNavigationForm form;

	@Override
	protected void onDestroy() {

		if(form != null){
			form.cleanup();
			form = null;
		}

		super.onDestroy();
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection_navi);


		form = new RedmineNavigationForm(this);
		form.setupEvents();

		form.buttonSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!form.Validate())
					return;
				ConnectionNaviResultIntent intent = new ConnectionNaviResultIntent(new Intent());
				intent.setAuthID(form.getAuthID());
				intent.setAuthPassword(form.getAuthPassword());
				intent.setUnsafeSSL(form.isUnsafeSLL());
				intent.setToken(form.getApiKey());
				setResult(RESULT_OK, intent.getIntent());
				finish();
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();

		ConnectionNaviResultIntent intent = new ConnectionNaviResultIntent(getIntent());
		form.setDefaultAuthentication(intent.getAuthID(),intent.getAuthPassword());
		form.loadUrl(intent.getUrl());
	}

}

