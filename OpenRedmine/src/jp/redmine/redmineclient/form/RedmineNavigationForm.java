package jp.redmine.redmineclient.form;

import com.andreabaccega.widget.FormEditText;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.external.lib.MyWebViewClient;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.Uri.Builder;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

public class RedmineNavigationForm extends FormHelper {
	private Activity activity;
	public WebView  webView ;
	protected LocalWebViewClient webClient;
	public FormEditText editToken;
	public Button buttonSave;
	protected LinearLayout header;
	protected String AuthID = "";
	protected String AuthPassword = "";

	public RedmineNavigationForm(Activity activity){
		this.activity = activity;
		this.setup();
		this.setupDefaults();

	}


	public void setup(){
		webView = (WebView)activity.findViewById(R.id.webView);
		editToken = (FormEditText)activity.findViewById(R.id.editToken);
		buttonSave = (Button)activity.findViewById(R.id.buttonSave);
		webClient = new LocalWebViewClient(activity, webView);
		header = (LinearLayout)activity.findViewById(R.id.layoutHeader);
	}


	public boolean Validate(){
		return ValidateForm(editToken);
	}

	protected void setHeaderVisible(boolean isVisible){
		performSetVisible(header, isVisible);
	}

	public String getAuthID(){
		return AuthID;
	}
	protected void setAuthID(String id){
		AuthID = id;
	}
	public String getAuthPassword(){
		return AuthPassword;
	}
	protected void setAuthPassword(String pass){
		AuthPassword = pass;
	}
	public boolean isUnsafeSLL(){
		return webClient != null ? webClient.isSSLError : false;
	}
	public void setUnsafeSSL(boolean isUnsafe){
		if(webClient!=null)
			webClient.isSSLError = isUnsafe;
	}
	public String getApiKey(){
		return editToken.getText().toString();
	}
	public void setApiKey(String token){
		editToken.setText(token);
	}
	protected void performAction(String token){
		editToken.setText(token);
		buttonSave.performClick();
	}

	@SuppressLint({ "SetJavaScriptEnabled" })
	public void setupEvents(){
		CustomWebChromeClient client = new CustomWebChromeClient();
		webView.setWebViewClient(webClient);
		webView.setWebChromeClient(client);
		webView.getSettings().setJavaScriptEnabled(true);

	}

	public void setupDefaults(){

	}

	public void cleanup(){
		if(webView != null){
			webView.destroy();
			webView = null;
		}
	}

	protected void stopActivity(){
		cleanup();
		activity.finish();
	}


	public void loadUrl(String url){
		webClient.UserID = AuthID;
		webClient.Password = AuthPassword;
		Builder data = Uri.parse(url).buildUpon();
		data.appendPath("login");
		webClient.LimitUrl = url;
		webView.loadUrl(data.toString());
	}

	public void setDefaultAuthentication(String id, String password){
		AuthID = id;
		AuthPassword = password;
	}

	private class LocalWebViewClient extends MyWebViewClient{
		public String LimitUrl = "";

		public LocalWebViewClient(Context context, WebView webview) {
			super(context, webview);
		}

		@Override
		public void onPageStarted( WebView view, String url, Bitmap favicon ) {
			if(!url.startsWith(LimitUrl)){
				view.stopLoading();
				stopActivity();
			}
			setHeaderVisible(true);
		}
		@Override
		public void onPageFinished(WebView view, String url) {

			if(url.endsWith("my/account")){
				// HTMLソース上のpinコードを取得するためのJavaScript
				String script = "javascript:";
				script += "var elem = document.getElementById('api-access-key');";
				script += "if(elem) alert(elem.childNodes[0].nodeValue);";
				view.loadUrl(script);
			} else if(!url.endsWith("login")){
				view.loadUrl(LimitUrl + "/my/account");
			}
			setHeaderVisible(false);
		}

		@Override
		protected void afterSetHttpAuth(String id, String password) {
			setAuthID(id);
			setAuthPassword(password);
		}
	}

	// JavaScript：alertをAndroid側でハンドリングするための仕組みである
	// WebChromeClient#onJsAlert()メソッドをオーバーライドして
	private class CustomWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			if (message == null) {
				return false;
			}
			performAction(message);
			result.confirm();
			// アラートダイアログをこのメソッド内で処理したか
			// falseを返すとAndroid APIによるデフォルトのアラートダイアログが表示されるため、今回はtrueを返す
			return true;
		}

	}
}

