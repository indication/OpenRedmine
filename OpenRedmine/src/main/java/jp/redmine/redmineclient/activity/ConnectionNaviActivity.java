package jp.redmine.redmineclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.form.RedmineNavigationForm;
import jp.redmine.redmineclient.param.ConnectionNaviResultArgument;


public class ConnectionNaviActivity extends FragmentActivity {
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
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.page_connection_navi);


		form = new RedmineNavigationForm(this);
		form.setupEvents();

		form.buttonSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!form.Validate())
					return;
				ConnectionNaviResultArgument intent = new ConnectionNaviResultArgument();
				intent.setIntent(new Intent());
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

		ConnectionNaviResultArgument intent = new ConnectionNaviResultArgument();
		intent.setIntent(getIntent());
		form.setDefaultAuthentication(intent.getAuthID(),intent.getAuthPassword());
		form.setUnsafeSSL(intent.isUnsafeSSL());
		form.setApiKey(intent.getToken());
		form.loadUrl(intent.getUrl());
	}

}

