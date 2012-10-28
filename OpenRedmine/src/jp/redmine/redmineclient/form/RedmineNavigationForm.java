package jp.redmine.redmineclient.form;

import com.andreabaccega.widget.FormEditText;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.external.lib.MyWebViewClient;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Handler;
import android.os.Message;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;

public class RedmineNavigationForm {
	private Activity activity;
	public WebView  webView ;
	protected LocalWebViewClient webClient;
	public FormEditText editToken;
	public Button buttonSave;
	public Handler handler;
	protected String AuthID = "";
	protected String AuthPassword = "";

	interface MessageTypes {
		public int status = 1;
		public int apikey = 2;
		public int authid = 3;
		public int authpassword = 4;
	}

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
	}

	public boolean Validate(){
		return editToken.testValidity();
	}

	public String getAuthID(){
		return AuthID;
	}
	public String getAuthPassword(){
		return AuthPassword;
	}
	public boolean isUnsafeSLL(){
		return webClient != null ? webClient.isSSLError : false;
	}
	public String getApiKey(){
		return editToken.getText().toString();
	}


	@SuppressLint({ "HandlerLeak", "SetJavaScriptEnabled" })
	public void setupEvents(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what){
				case MessageTypes.status:
					cleanup();
					activity.finish();
					break;
				case MessageTypes.apikey:
					editToken.setText((String)msg.obj);
					buttonSave.performClick();
					break;
				case MessageTypes.authid:
					AuthID = (String)msg.obj;
					break;
				case MessageTypes.authpassword:
					AuthPassword = (String)msg.obj;
					break;

				}
			}
		};
		CustomWebChromeClient client = new CustomWebChromeClient();
		client.setHandler(handler);
		webClient.setHandler(handler);
		webView.setWebViewClient(webClient);
		webView.setWebChromeClient(client);
		webView.getSettings().setJavaScriptEnabled(true);

	}

	public void setupDefaults(){

	}

	public void cleanup(){

		webView.destroy();
		handler = null;
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
		protected Handler handler;

		public LocalWebViewClient(Context context, WebView webview) {
			super(context, webview);
		}

		public void setHandler(Handler hand){
			handler = hand;
		}

		@Override
		public void onPageStarted( WebView view, String url, Bitmap favicon ) {
			if(!url.startsWith(LimitUrl)){
				view.stopLoading();
				Message msg = new Message();
				msg.what = MessageTypes.status;
				msg.obj = url;
				handler.sendMessage(msg);
			}
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
		}

		@Override
		protected void afterSetHttpAuth(String id, String password) {

			if(handler != null ){
				Message msg = new Message();
				msg.what = MessageTypes.authid;
				msg.obj = id;
				handler.sendMessage(msg);
				msg = new Message();
				msg.what = MessageTypes.authpassword;
				msg.obj = password;
				handler.sendMessage(msg);
			}
		}
	}

	// JavaScript：alertをAndroid側でハンドリングするための仕組みである
	// WebChromeClient#onJsAlert()メソッドをオーバーライドして
	private class CustomWebChromeClient extends WebChromeClient {

		protected Handler handler;
		public void setHandler(Handler hand){
			handler = hand;
		}
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

			if (message == null) {
				return false;
			}

			if(handler != null ){
				Message msg = new Message();
				msg.what = MessageTypes.apikey;
				msg.obj = message;
				handler.sendMessage(msg);
			}

			result.confirm();
			// アラートダイアログをこのメソッド内で処理したか
			// falseを返すとAndroid APIによるデフォルトのアラートダイアログが表示されるため、今回はtrueを返す
			return true;
		}
	}
}

