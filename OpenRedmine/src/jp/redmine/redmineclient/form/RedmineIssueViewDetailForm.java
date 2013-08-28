package jp.redmine.redmineclient.form;

import java.math.BigDecimal;
import java.util.Date;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.form.helper.TextileHelper;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class RedmineIssueViewDetailForm extends RedmineIssueDetailBaseForm {
	public TextView textPrivate;
	public TextView textCreated;
	public TextView textProgress;
	public TextView textTimeEstimate;
	public TextView textTimeEntry;
	public WebView webView;
	public TextileHelper webViewHelper;
	public RedmineIssueViewDetailForm(View activity){
		super(activity);
		this.setup(activity);
	}


	public void setupWebView(IntentAction act){
		webViewHelper = new TextileHelper(webView);
		webViewHelper.setup();
		webViewHelper.setAction(act);
	}

	public void setup(View view){
		super.setup(view);
		textPrivate = (TextView)view.findViewById(R.id.textPrivate);
		textCreated = (TextView)view.findViewById(R.id.textCreated);
		textProgress = (TextView)view.findViewById(R.id.textProgress);
		textTimeEstimate = (TextView)view.findViewById(R.id.textEstimate);
		textTimeEntry = (TextView)view.findViewById(R.id.textTimeEntry);
		webView = (WebView)view.findViewById(R.id.webView);
		webView.getSettings().setBlockNetworkLoads(true);
	}



	public void setValue(RedmineIssue rd){
		super.setValue(rd);
		if(rd.getConnectionId() != null)
			webViewHelper.setContent(rd.getConnectionId(), rd.getDescription());
		setUserNameDateTime(textCreated,R.string.ticket_created_by,rd.getAuthor(),rd.getCreated());
		setUserNameDateTime(textModified,R.string.ticket_modified_by,null,rd.getModified());
		setUserName(textAssignedTo,rd.getAssigned());
		setPrivate(rd.isPrivate());
		setTime(textTimeEstimate,R.string.ticket_time_estimate,rd.getEstimatedHours());


	}
	public void setPrivate(boolean isPrivate){
		performSetVisible(textPrivate, isPrivate);
	}

	public void setValueTimeEntry(BigDecimal val){
		setTime(textTimeEntry,R.string.ticket_time_estimate,val.doubleValue());
	}
	protected void setUserNameDateTime(TextView v,int format,RedmineUser ct,Date date){
		String ret = v.getContext().getString(format, convertUserName(v,ct), convertDateTime(v, date));
		v.setText(ret);
	}

	@Override
	public void setProgress(Short progress, Short donerate) {
		super.setProgress(progress, donerate);
		textProgress.setText(textProgress.getContext().getString(R.string.format_progress,donerate));
	}
}

