package jp.redmine.redmineclient.form;

import java.math.BigDecimal;
import java.util.Date;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.form.helper.FormHelper;
import jp.redmine.redmineclient.form.helper.TextileHelper;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RedmineIssueViewDetailForm extends FormHelper {
	public TextView textTracker;
	public TextView textCategory;
	public TextView textPrivate;
	public TextView textAuthor;
	public TextView textStatus;
	public TextView textProgress;
	public TextView textPriority;
	public TextView textAssignedTo;
	public TextView textDateFrom;
	public TextView textDateTo;
	public TextView textVersion;
	public TextView textModified;
	public TextView textTimeEstimate;
	public TextView textTimeEntry;
	public LinearLayout linearTimeEntry;
	public WebView webView;
	public TextileHelper webViewHelper;
	public ProgressBar progressBar;
	public RedmineIssueViewDetailForm(View activity){
		this.setup(activity);
	}


	public void setupWebView(IntentAction act){
		webViewHelper = new TextileHelper(webView);
		webViewHelper.setup();
		webViewHelper.setAction(act);
	}

	public void setup(View view){
		textTracker = (TextView)view.findViewById(R.id.textTracker);
		textCategory = (TextView)view.findViewById(R.id.textCategory);
		textPrivate = (TextView)view.findViewById(R.id.textPrivate);
		textPriority = (TextView)view.findViewById(R.id.textPriority);
		textAuthor = (TextView)view.findViewById(R.id.textAuthor);
		textStatus = (TextView)view.findViewById(R.id.textStatus);
		textProgress = (TextView)view.findViewById(R.id.textProgress);
		textAssignedTo = (TextView)view.findViewById(R.id.textAssignedTo);
		textDateFrom = (TextView)view.findViewById(R.id.textDateFrom);
		textDateTo = (TextView)view.findViewById(R.id.textDateTo);
		textVersion = (TextView)view.findViewById(R.id.textVersion);
		textModified = (TextView)view.findViewById(R.id.textModified);
		textTimeEstimate = (TextView)view.findViewById(R.id.textEstimate);
		textTimeEntry = (TextView)view.findViewById(R.id.textTimeEntry);
		linearTimeEntry = (LinearLayout)view.findViewById(R.id.linearTimeEntry);
		progressBar = (ProgressBar)view.findViewById(R.id.progressissue);
		webView = (WebView)view.findViewById(R.id.webView);
		webView.getSettings().setBlockNetworkLoads(true);
	}

	public void setTracker(RedmineTracker tk){
		textTracker.setText(tk == null ? "" : tk.getName());
	}
	public void setCategory(RedmineProjectCategory ct){
		textCategory.setText(ct == null ? "" : ct.getName());
	}
	public void setVersion(RedmineProjectVersion vr){
		textVersion.setText(vr == null ? "" : vr.getName());
	}
	public void setPrivate(boolean isPrivate){
		performSetVisible(textPrivate, isPrivate);
	}
	public void setStatus(RedmineStatus status){
		textStatus.setText(status == null ? "" : status.getName());
	}
	public void setPriority(RedminePriority pr){
		textPriority.setText(pr == null ? "" : pr.getName());
	}

	public void setProgress(Short progress,Short donerate){
		progressBar.setMax(100);
		progressBar.setProgress(progress == null ? 0 : progress);
		progressBar.setSecondaryProgress(donerate == null ? 0 : donerate);
		textProgress.setText(textProgress.getContext().getString(R.string.format_progress,donerate));
	}

	public void setValue(RedmineIssue rd){
		if(rd.getConnectionId() != null)
			webViewHelper.setContent(rd.getConnectionId(), rd.getDescription());
		setDate(textDateFrom,rd.getDateStart());
		setDate(textDateTo,rd.getDateDue());
		setTracker(rd.getTracker());
		setUserNameDateTime(textAuthor,R.string.ticket_created_by,rd.getAuthor(),rd.getCreated());
		setUserNameDateTime(textModified,R.string.ticket_modified_by,null,rd.getModified());
		setUserName(textAssignedTo,rd.getAssigned());
		setStatus(rd.getStatus());
		setPriority(rd.getPriority());
		setPrivate(rd.isPrivate());
		setCategory(rd.getCategory());
		setVersion(rd.getVersion());
		setProgress(rd.getProgressRate(),rd.getDoneRate());
		setTime(textTimeEstimate,R.string.ticket_time_estimate,rd.getEstimatedHours());


	}

	public void setValueTimeEntry(BigDecimal val){
		setTime(textTimeEntry,R.string.ticket_time_estimate,val.doubleValue());
	}
	protected void setUserNameDateTime(TextView v,int format,RedmineUser ct,Date date){
		String ret = v.getContext().getString(format, convertUserName(v,ct), convertDateTime(v, date));
		v.setText(ret);
	}

}

