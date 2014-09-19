package jp.redmine.redmineclient.adapter.form;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineRecentIssue;
import jp.redmine.redmineclient.form.helper.FormHelper;

public class RecentIssueForm extends FormHelper {
	public TextView textSubject;
	public TextView textIssueid;
	public TextView textStatus;
	public TextView textModified;
	public ProgressBar progressBar;
	public RecentIssueForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = (TextView)view.findViewById(R.id.textSubject);
		textIssueid = (TextView)view.findViewById(R.id.textIssueid);
		textStatus = (TextView)view.findViewById(R.id.textStatus);
		textModified = (TextView)view.findViewById(R.id.textModified);
		progressBar = (ProgressBar)view.findViewById(R.id.progressissue);

	}
	public void setValue(RedmineRecentIssue rd){
		RedmineIssue issue = rd.getIssue();
		textSubject.setText(issue.getSubject());
		textIssueid.setText(issue.getIssueId() == null ? "" : "#"+issue.getIssueId().toString());
		setMasterName(textStatus, issue.getStatus());
		setProgress(issue.getProgressRate(),issue.getDoneRate());
		setDateTimeSpan(textModified, rd.getModified());

		boolean isEnabled = true;
		if(issue.getStatus() != null && issue.getStatus().isIs_close()){
			isEnabled = false;
		}
		performSetEnabled((ViewGroup)(textSubject.getParent()), isEnabled);
	}

	public void setProgress(Short progress,Short donerate){
		progressBar.setMax(100);
		progressBar.setProgress(progress == null ? 0 : progress);
		progressBar.setSecondaryProgress(donerate == null ? 0 : donerate);
	}

}

