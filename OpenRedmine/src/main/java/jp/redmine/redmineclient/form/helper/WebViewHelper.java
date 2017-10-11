package jp.redmine.redmineclient.form.helper;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;

public class WebViewHelper {
	private WebviewActionInterface action;
	private RedmineConvertToHtmlHelper converter = new RedmineConvertToHtmlHelper();
	private Pattern patternIntent = Pattern.compile(RedmineConvertToHtmlHelper.URL_PREFIX);
	public void setup(WebView view){
		setupWebView(view);
		setupHandler(view);
	}

	protected void setupWebView(WebView view){
		view.getSettings().setBlockNetworkLoads(true);
	}
	public void setAction(WebviewActionInterface act){
		action = act;
	}

	protected void setupHandler(WebView view){
		view.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Matcher m = patternIntent.matcher(url);
				if(m.find()){
					return  RedmineConvertToHtmlHelper.kickAction(action, m.replaceAll(""));
				} else if (action != null) {
					return action.url(url, null);
				} else {
					return super.shouldOverrideUrlLoading(view, url);
				}
			}
		});
	}

	public void setContent(WebView view, WikiType type, final int connectionid, final long project, final String text){
		String inner = converter.parse(text, type, connectionid, project);
		view.loadDataWithBaseURL("", HtmlHelper.getHtml(view.getContext(),inner,""), "text/html", "UTF-8", "");
	}

	public void setContent(WebView view, String text, WikiType type){
		String inner = converter.parse(text, type);
		view.loadDataWithBaseURL("", HtmlHelper.getHtml(view.getContext(),inner,""), "text/html", "UTF-8", "");
	}
}
