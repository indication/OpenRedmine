package jp.redmine.redmineclient.form;

import java.math.BigDecimal;
import java.util.Date;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.form.helper.TextileHelper;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import android.view.View;
import android.webkit.WebView;
import android.widget.TableRow;
import android.widget.TextView;

public class RedmineIssueViewDetailForm extends RedmineIssueDetailBaseForm {
	public TextView textProject;
	public TextView textPrivate;
	public TextView textCreated;
	public TextView textProgress;
	public TextView textTimeEstimate;
	public TextView textTimeEntry;
	public TextView textCategory;
	public TableRow rowCategory;
	public TableRow rowDate;
	public TableRow rowVersion;
	public TableRow rowAssigned;
	public TableRow rowTimeEntry;
	public TableRow rowProject;
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
		textProject = (TextView)view.findViewById(R.id.textProject);
		textPrivate = (TextView)view.findViewById(R.id.textPrivate);
		textCreated = (TextView)view.findViewById(R.id.textCreated);
		textProgress = (TextView)view.findViewById(R.id.textProgress);
		textTimeEstimate = (TextView)view.findViewById(R.id.textEstimate);
		textTimeEntry = (TextView)view.findViewById(R.id.textTimeEntry);
		textCategory = (TextView)view.findViewById(R.id.textCategory);
		rowCategory = (TableRow)view.findViewById(R.id.rowCategory);
		rowDate = (TableRow)view.findViewById(R.id.rowDate);
		rowVersion = (TableRow)view.findViewById(R.id.rowVersion);
		rowAssigned = (TableRow)view.findViewById(R.id.rowAssigned);
		rowTimeEntry = (TableRow)view.findViewById(R.id.rowTimeEntry);
		rowProject = (TableRow)view.findViewById(R.id.rowProject);
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
		setMasterName(textCategory,rd.getCategory());
		setVisible(rowCategory,rd.getCategory());
		setVisible(rowVersion,rd.getVersion());
		setVisible(rowAssigned,rd.getAssigned());
		setVisible(rowDate,!(rd.getDateStart() == null && rd.getDateDue() == null));
		setVisible(rowTimeEntry,rd.getEstimatedHours() != 0);
		setMasterName(textProject, rd.getProject());


	}
	protected void setVisible(TableRow row, IMasterRecord record){
		setVisible(row,!(record == null || record.getId() == null));
	}
	protected void setVisible(TableRow row, boolean isVisible){
		row.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}
	
	public void setPrivate(boolean isPrivate){
		performSetVisible(textPrivate, isPrivate);
	}

	public void setValueTimeEntry(BigDecimal val){
		setTime(textTimeEntry,R.string.ticket_time_estimate,val.doubleValue());
		setVisible(rowTimeEntry,val.doubleValue() != 0 || "0".equals(textTimeEstimate.getText().toString()));
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

