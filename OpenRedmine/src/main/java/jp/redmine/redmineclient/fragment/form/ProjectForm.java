package jp.redmine.redmineclient.fragment.form;

import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.helper.FormHelper;
import jp.redmine.redmineclient.form.helper.TextViewHelper;
import jp.redmine.redmineclient.form.helper.WebViewHelper;

public class ProjectForm extends FormHelper {
	public TextView textProject;
	public TextView textStatus;
	public TextView textHomepage;
	public TextView textCreated;
	public TextView textModified;
	public TextViewHelper textViewHelper = new TextViewHelper();
	private WebViewHelper webViewHelper = new WebViewHelper();
	private WebView webView;
	public ProjectForm(View activity){
		this.setup(activity);
		this.setupEvents();
	}

	public void setupWebView(WebviewActionInterface act){
		textViewHelper.setup(textHomepage);
		textViewHelper.setAction(act);
		webViewHelper.setup(webView);
		webViewHelper.setAction(act);
	}

	public void setup(View view){
		textProject = (TextView)view.findViewById(R.id.textProject);
		textStatus = (TextView)view.findViewById(R.id.textStatus);
		textHomepage = (TextView)view.findViewById(R.id.textHomepage);
		textCreated = (TextView)view.findViewById(R.id.textCreated);
		textModified = (TextView)view.findViewById(R.id.textModified);
		webView = (WebView) view.findViewById(R.id.webView);
	}

	public void setupEvents(){

	}

	public void setValue(RedmineConnection con, RedmineProject rd){
		setMasterName(textProject, rd);
		textStatus.setText(textStatus.getContext().getString(rd.getStatus().getResourceId()));
		textViewHelper.setContent(textHomepage, rd.getConnectionId(), rd.getId(), nvl(rd.getHomepage()));
		webViewHelper.setContent(webView, con.getWikiType(), rd.getConnectionId(), rd.getId(), nvl(rd.getDescription()));
		setDateTime(textCreated, rd.getCreated());
		setDateTime(textModified, rd.getModified());
	}

	protected String nvl(String input){
		if(TextUtils.isEmpty(input))
			return "";
		return input;
	}


}

