package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.form.helper.TextileHelper;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class RedmineJournalListItemForm extends FormHelper {
	public TextView textUser;
	public TextView textDate;
	public WebView webView;
	public TextileHelper webViewHelper;
	public RedmineJournalListItemForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textUser = (TextView)view.findViewById(R.id.user);
		textDate = (TextView)view.findViewById(R.id.date);
		webView = (WebView)view.findViewById(R.id.webView);

	}

	public void setupWebView(IntentAction act){
		webViewHelper = new TextileHelper(webView);
		webViewHelper.setup();
		webViewHelper.setAction(act);
	}

	public void setValue(RedmineJournal jr){
		webView.requestLayout();
		webViewHelper.setContent(jr.getConnectionId(), jr.getNotes());
		setUserName(textUser, jr.getUser());
		setDateTime(textDate,jr.getCreated());
	}


}

