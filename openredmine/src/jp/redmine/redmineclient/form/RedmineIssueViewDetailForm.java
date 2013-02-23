package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RedmineIssueViewDetailForm extends FormHelper {
	private View view;
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
	public TextView textDescription;
	public ProgressBar progressBar;
	public RedmineIssueViewDetailForm(View activity){
		this.view = activity;
		this.setup();
	}


	public void setup(){
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
		textDescription = (TextView)view.findViewById(R.id.textDescription);
		progressBar = (ProgressBar)view.findViewById(R.id.progressissue);
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

	public void setProgress(short progress,short donerate){
		progressBar.setMax(100);
		progressBar.setProgress(progress);
		progressBar.setSecondaryProgress(donerate);
		textProgress.setText(String.valueOf(donerate).concat("%"));
	}

	public void setValue(RedmineIssue rd){
		textDescription.setText(rd.getDescription());
		setDate(textDateFrom,rd.getDateStart());
		setDate(textDateTo,rd.getDateDue());
		setTracker(rd.getTracker());
		setUserName(textAuthor,rd.getAuthor());
		setUserName(textAssignedTo,rd.getAssigned());
		setStatus(rd.getStatus());
		setPriority(rd.getPriority());
		setPrivate(rd.isPrivate());
		setCategory(rd.getCategory());
		setVersion(rd.getVersion());
		setProgress(rd.getProgressRate(),rd.getDoneRate());

	}

}

