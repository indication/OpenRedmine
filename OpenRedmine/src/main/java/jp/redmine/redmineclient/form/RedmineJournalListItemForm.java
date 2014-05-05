package jp.redmine.redmineclient.form;

import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedmineJournalChanges;
import jp.redmine.redmineclient.form.helper.FormHelper;
import jp.redmine.redmineclient.form.helper.WebViewHelper;

public class RedmineJournalListItemForm extends FormHelper {
	public WebView webView;
	public LinearLayout formChanges;
	public WebViewHelper webViewHelper;
	public RedmineJournalListItemForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		webView = (WebView)view.findViewById(R.id.webView);
		formChanges = (LinearLayout)view.findViewById(R.id.formChanges);

	}

	public void setupWebView(WebviewActionInterface act){
		webViewHelper = new WebViewHelper();
		webViewHelper.setup(webView);
		webViewHelper.setAction(act);
	}

	public void setValue(RedmineJournal jr, long projectid){
		webViewHelper.setContent(webView, jr.getConnectionId(), projectid, jr.getNotes());
		setChangesets(formChanges, jr.changes);
	}

	static protected void setChangesets(LinearLayout view, List<RedmineJournalChanges> changes){
		view.removeAllViews();
		if(changes == null)
			return;
		for(RedmineJournalChanges item : changes){
			addChangeset(view, item);
		}
	}
	static protected void addChangeset(LinearLayout view, RedmineJournalChanges changes){
		if(changes.getResourceId() == null)
			return;
		int resId;
		if(changes.getMasterBefore() != null && changes.getMasterAfter() != null){
			resId = R.string.changes_from_to;
		} else if(changes.getMasterBefore() == null && changes.getMasterAfter() != null){
			resId = R.string.changes_set_to;
		} else if(changes.getMasterBefore() != null && changes.getMasterAfter() == null){
			resId = R.string.changes_remove_from;
		} else {
			return;
		}
		TextView v = new TextView(view.getContext());
		String name = view.getContext().getString(changes.getResourceId());
		String result = view.getContext().getString(resId, name, changes.getMasterNameBefore(), changes.getMasterNameAfter());
		v.setText(Html.fromHtml(result));
		view.addView(v);
	}

}

