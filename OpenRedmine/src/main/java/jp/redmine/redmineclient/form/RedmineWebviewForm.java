package jp.redmine.redmineclient.form;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Build;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.form.helper.FormHelper;
import jp.redmine.redmineclient.form.helper.RedmineWebViewClient;

public class RedmineWebviewForm extends FormHelper {
	private static String DB_NAME="RedmineWebViewCache.db";
	public WebView  webView ;
	RedmineWebViewClient webClient;

	public RedmineWebviewForm(Activity view){
		setup(view);
	}

	public void setup(Activity view){
		webView = view.findViewById(R.id.webView);
	}

	@SuppressLint({ "SetJavaScriptEnabled" })
	public void setupEvents(){
		WebChromeClient client = new WebChromeClient();
		webView.setWebChromeClient(client);
		android.webkit.WebSettings setting = webView.getSettings();
		//Setup built-in features
		setting.setUseWideViewPort(true);
		setting.setLoadWithOverviewMode(true);
		setting.setDomStorageEnabled(true);

		//Setup security settings with caution
		setting.setGeolocationEnabled(false);
		setting.setJavaScriptEnabled(true);
		//Assets and resources are still accessible using file:///android_asset and file:///android_res
		setting.setAllowFileAccess(false);

		//Enable cache access
		setting.setAppCachePath(webView.getContext().getCacheDir().getPath() + "/" + DB_NAME);
		setting.setAppCacheEnabled(true);

		setEventsV8();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void setEventsV8(){
		android.webkit.WebSettings setting = webView.getSettings();
		setting.setAllowFileAccessFromFileURLs(false);
	}

	public void cleanup(){
		if(webView != null){
			webView.stopLoading();
			webView.destroy();
			webView = null;
		}
		if(webClient != null){
			webClient.resetCookie();
			webClient = null;
		}
	}

	public void loadUrl(RedmineConnection con, String url, RedmineWebViewClient.IConnectionEventHadler handler){
		Builder data = Uri.parse(url).buildUpon();
		webClient = new RedmineWebViewClient(con);
		webClient.setEventHandler(handler);
		webView.setWebViewClient(webClient);
		webView.loadUrl(data.toString(), RedmineWebViewClient.generateRedmineHeader(con));

	}

	public String getUrl(){
		return webView.getUrl();
	}
}

