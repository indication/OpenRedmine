package jp.redmine.redmineclient.adapter.form;

import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Date;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.form.helper.TextViewHelper;

public class IssueDetailForm extends IssueBaseForm {
	public TextView textProject;
	public TextView textPrivate;
	public TextView textCreated;
	public TextView textClosed;
	public TextView textProgress;
	public TextView textTimeEstimate;
	public TextView textTimeEntry;
	public TextView textCategory;
	public TextView labelCategory;
	public TextView labelDate;
	public TextView labelDateArrow;
	public TextView labelVersion;
	public TextView labelAssignedTo;
	public TextView labelTime;
	public TextView labelTimeSlice;
	public TextView labelClosed;
	public TextView labelProject;
	public TextView textView;
	public TextViewHelper textViewHelper;
	public IssueDetailForm(View activity){
		super(activity);
		this.setup(activity);
	}


	public void setupWebView(WebviewActionInterface act){
		textViewHelper = new TextViewHelper();
		textViewHelper.setup(textView);
		textViewHelper.setAction(act);
	}

	public void setup(View view){
		super.setup(view);
		textProject = view.findViewById(R.id.textProject);
		textPrivate = view.findViewById(R.id.textPrivate);
		textCreated = view.findViewById(R.id.textCreated);
		textProgress = view.findViewById(R.id.textProgress);
		textTimeEstimate = view.findViewById(R.id.textEstimate);
		textTimeEntry = view.findViewById(R.id.textTimeEntry);
		textCategory = view.findViewById(R.id.textCategory);
		labelCategory = view.findViewById(R.id.labelCategory);
		labelDate = view.findViewById(R.id.labelDate);
		labelDateArrow = view.findViewById(R.id.labelDateArrow);
		labelVersion = view.findViewById(R.id.labelVersion);
		labelAssignedTo = view.findViewById(R.id.labelAssignedTo);
		labelTime = view.findViewById(R.id.labelTime);
		labelTimeSlice = view.findViewById(R.id.labelTimeSlice);
		labelProject = view.findViewById(R.id.labelProject);
		textClosed = view.findViewById(R.id.textClosed);
		labelClosed = view.findViewById(R.id.labelClosed);
		textView = view.findViewById(R.id.textView);
	}



	public void setValue(RedmineIssue rd){
		super.setValue(rd);
		if(rd.getConnectionId() != null)
			textViewHelper.setContent(textView, rd.getConnectionId(), rd.getProject().getProjectId(), rd.getDescription());
		setUserNameDateTime(textCreated,R.string.ticket_created_by,rd.getAuthor(),rd.getCreated());
		setUserNameDateTime(textModified,R.string.ticket_modified_by,null,rd.getModified());
		setUserName(textAssignedTo,rd.getAssigned());
		setPrivate(rd.isPrivate());
		setTime(textTimeEstimate,R.string.ticket_time_estimate,rd.getEstimatedHours());
		setMasterName(textCategory,rd.getCategory());
		setDateTime(textClosed, rd.getClosed());
		setVisible(rd.getCategory(), labelCategory, textCategory);
		setVisible(rd.getVersion(), labelVersion, textVersion);
		setVisible(rd.getAssigned(), labelAssignedTo, textAssignedTo);
		setVisible(rd.getClosed() != null, labelClosed, textClosed);
		setVisible(!(rd.getDateStart() == null && rd.getDateDue() == null), labelDate,textDateFrom, textDateTo, labelDateArrow);
		setVisible(rd.getEstimatedHours() != 0, labelTime, textTimeEstimate, labelTimeSlice);
		setMasterName(textProject, rd.getProject());


	}
	protected void setVisible(IMasterRecord record, View ... views){
		setVisible(!(record == null || record.getId() == null), views);
	}
	protected void setVisible(boolean isVisible, View ... views){
		for(View view : views){
			view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
		}
	}
	
	public void setPrivate(boolean isPrivate){
		performSetVisible(textPrivate, isPrivate);
	}

	public void setValueTimeEntry(BigDecimal val){
		setTime(textTimeEntry,R.string.ticket_time_estimate,val.doubleValue());
		setVisible(val.doubleValue() != 0 || "0".equals(textTimeEstimate.getText().toString()), labelTime, textTimeEntry);
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

