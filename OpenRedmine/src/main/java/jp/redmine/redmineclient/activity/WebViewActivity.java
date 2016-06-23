package jp.redmine.redmineclient.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import org.apache.commons.lang3.StringUtils;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.form.RedmineWebviewForm;
import jp.redmine.redmineclient.form.helper.RedmineWebViewClient;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.WebArgument;

public class WebViewActivity extends ActionBarActivity {
	private static final String TAG = WebViewActivity.class.getSimpleName();
	public WebViewActivity(){
		super();
	}
	private RedmineWebviewForm form;

	@Override
	public void onDestroy() {
		setVisible(false);
		if(form != null){
			form.cleanup();
			form = null;
		}

		super.onDestroy();
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		ActivityHelper.setupTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_connection_web);
		ActionBar actionBar = getSupportActionBar();
	    if(actionBar != null)
			actionBar.setDisplayHomeAsUpEnabled(true);

		form = new RedmineWebviewForm(this);
		form.setupEvents();
	}

	@Override
	protected void onStart() {
		super.onStart();

		WebArgument intent = new WebArgument();
		intent.setIntent(getIntent());
		RedmineConnection con = ConnectionModel.getItem(this, intent.getConnectionId());

		if (con.getId() != null) {
			String url = "";
			if(StringUtils.isEmpty(intent.getUrl())){
				url = con.getUrl();
			} else if (intent.getUrl().startsWith("/")) {
				url = con.getUrl() + intent.getUrl();
			} else if (intent.getUrl().startsWith(con.getUrl())) {
				url = intent.getUrl();
			}
			if(!StringUtils.isEmpty(url)) {
				form.loadUrl(con, url, new RedmineWebViewClient.IConnectionEventHadler() {
					@Override
					public boolean actionNotCurrentConnection(RedmineWebViewClient client, WebView view, String url) {
						Intent external = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(external);
						return false;
					}
				});
				return;
			}
		}
		Intent external = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getUrl()));
		startActivity(external);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}

