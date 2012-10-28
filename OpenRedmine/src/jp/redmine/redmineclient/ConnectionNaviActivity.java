package jp.redmine.redmineclient;

import jp.redmine.redmineclient.form.RedmineNavigationForm;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ConnectionNaviActivity extends Activity {
	public static final String INTENT_STR_URL = "URL";
	public static final String INTENT_STR_ID = "ID";
	public static final String INTENT_STR_PASS = "PASS";
	public static final String INTENT_STR_TOKEN = "TOKEN";
	public static final String INTENT_BOOL_UNSAFESSL = "UNSAFE_SSL";
	private RedmineNavigationForm form;
	private String currentUrl;
	private String currentId;
	private String currentPassword;
	private SelectDataTask task ;

	@Override
	protected void onDestroy() {
		if(task != null && task.getStatus() == Status.RUNNING){
			task.cancel(false);
		}

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

		Intent intent = getIntent();
		currentUrl = intent.getStringExtra(INTENT_STR_URL);
		currentId = intent.getStringExtra(INTENT_STR_ID);
		currentPassword = intent.getStringExtra(INTENT_STR_PASS);


		form.setDefaultAuthentication(currentId,currentPassword);

		task = new SelectDataTask();
		task.execute(currentUrl);

		form.buttonSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!form.Validate())
					return;
				Intent intent = new Intent();
				intent.putExtra(INTENT_STR_ID, form.getAuthID());
				intent.putExtra(INTENT_STR_PASS, form.getAuthPassword());
				intent.putExtra(INTENT_BOOL_UNSAFESSL, form.isUnsafeSLL());
				intent.putExtra(INTENT_STR_TOKEN, form.getApiKey());
				setResult(RESULT_OK, intent);
				finish();
			}
		});

	}

	private class SelectDataTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String ... params) {
			form.loadUrl(params[0]);
			return null;
		}

	}
}

