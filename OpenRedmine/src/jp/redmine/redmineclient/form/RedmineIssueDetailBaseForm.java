package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RedmineIssueDetailBaseForm extends FormHelper {
	public TextView textStatus;
	public TextView textAssignedTo;
	public TextView textTracker;
	public TextView textCategory;
	public TextView textPriority;
	public TextView textDateFrom;
	public TextView textDateTo;
	public TextView textVersion;
	public TextView textModified;
	public ProgressBar progressBar;
	public RedmineIssueDetailBaseForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textStatus = (TextView)view.findViewById(R.id.textStatus);
		textAssignedTo = (TextView)view.findViewById(R.id.textAssignedTo);
		progressBar = (ProgressBar)view.findViewById(R.id.progressissue);
		textTracker = (TextView)view.findViewById(R.id.textTracker);
		textPriority = (TextView)view.findViewById(R.id.textPriority);
		textCategory = (TextView)view.findViewById(R.id.textCategory);
		textDateFrom = (TextView)view.findViewById(R.id.textDateFrom);
		textDateTo = (TextView)view.findViewById(R.id.textDateTo);
		textVersion = (TextView)view.findViewById(R.id.textVersion);
		textModified = (TextView)view.findViewById(R.id.textModified);
	}


	public void setValue(RedmineIssue rd){
		setDate(textDateFrom,rd.getDateStart());
		setDate(textDateTo,rd.getDateDue());
		setDateTime(textModified, rd.getModified());
		progressBar.setMax(100);
		progressBar.setProgress(rd.getProgressRate());
		setMasterName(textAssignedTo, rd.getAssigned());
		setMasterName(textStatus, rd.getStatus());
		setMasterName(textTracker, rd.getTracker());
		setStatus(rd.getStatus());
		setCategory(rd.getCategory());
		setVersion(rd.getVersion());
		setProgress(rd.getProgressRate(),rd.getDoneRate());

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
	}
}

