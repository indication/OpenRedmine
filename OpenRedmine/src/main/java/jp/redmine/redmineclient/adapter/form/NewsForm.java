package jp.redmine.redmineclient.adapter.form;

import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.entity.RedmineNews;
import jp.redmine.redmineclient.form.helper.FormHelper;
import jp.redmine.redmineclient.form.helper.WebViewHelper;

public class NewsForm extends FormHelper {
	public WebView webView;
	public TextView textSubject;
	public WebViewHelper webViewHelper;
	public NewsForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		webView = (WebView)view.findViewById(R.id.webView);
		textSubject = (TextView)view.findViewById(R.id.textSubject);
	}

	public void setupWebView(WebviewActionInterface act){
		webViewHelper = new WebViewHelper();
		webViewHelper.setup(webView);
		webViewHelper.setAction(act);
	}

	public void setValue(RedmineNews jr){
		webViewHelper.setContent(webView, jr.getConnectionId(), jr.getProject().getId(), jr.getDescription());
		textSubject.setText(jr.getTitle());
	}

}

