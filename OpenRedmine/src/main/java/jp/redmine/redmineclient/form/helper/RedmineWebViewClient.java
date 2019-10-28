package jp.redmine.redmineclient.form.helper;

import android.net.http.SslError;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;

import jp.redmine.redmineclient.entity.RedmineConnection;

public class RedmineWebViewClient extends WebViewClient {
	private String loginCookie;
	private RedmineConnection mConnection;
	protected CookieManager cookieManager;
	protected IConnectionEventHadler mHandler;

	public interface IConnectionEventHadler {
		boolean actionNotCurrentConnection(RedmineWebViewClient client, WebView view, String url);
	}

	public RedmineWebViewClient(RedmineConnection con) {
		super();
		setConnection(con);
	}

	public void setConnection(RedmineConnection con){
		mConnection = con;
	}

	public void setEventHandler(IConnectionEventHadler handler){
		mHandler = handler;
	}

	public void resetCookie(){
		if(cookieManager!=null){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				cookieManager.removeAllCookies(null);
			} else {
				//noinspection deprecation
				cookieManager.removeAllCookie();
			}
			cookieManager = null;
		}
	}

	@Override
	public void onPageFinished( WebView view, String url ) {
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setCookie(url, loginCookie);
	}

	@Override
	public void onLoadResource( WebView view, String url ){
		cookieManager = CookieManager.getInstance();
		loginCookie = cookieManager.getCookie(url);
	}

	@Override
	public boolean shouldOverrideUrlLoading( WebView view, String url ) {
		if(url.startsWith(mConnection.getUrl())) {
			view.loadUrl(url, generateRedmineHeader(mConnection));
			return true;
		}
		if (mHandler != null && mHandler.actionNotCurrentConnection(this, view, url)){
			return true;
		} else {
			view.stopLoading();
			return false;
		}
	}

	static public HashMap<String, String> generateRedmineHeader(RedmineConnection con){
		HashMap<String, String> extraHeaders = new HashMap<>();
		setRedmineHeader(con, extraHeaders);
		return extraHeaders;
	}

	static public void setRedmineHeader(RedmineConnection con, HashMap<String, String> extraHeaders){
		extraHeaders.put("X-Redmine-API-Key", con.getToken());
	}

	@Override
	public void onReceivedSslError( WebView view, SslErrorHandler handler, SslError error ) {
		if(mConnection.isPermitUnsafe()){
			handler.proceed();
		} else {
			super.onReceivedSslError(view, handler, error);
		}
	}

	@Override
	public void onReceivedHttpAuthRequest( WebView view, final HttpAuthHandler handler, final String host, final String realm ){
		if(mConnection.isAuth()) {
			handler.proceed(mConnection.getAuthId(), mConnection.getAuthPasswd());
		} else {
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}
	}
}
