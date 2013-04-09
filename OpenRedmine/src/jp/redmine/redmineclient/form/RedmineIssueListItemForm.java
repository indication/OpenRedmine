package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RedmineIssueListItemForm extends FormHelper {
	public TextView textSubject;
	public TextView textStatus;
	public TextView textTicketid;
	public TextView textDescription;
	public TextView textBottomtext;
	public TextView textTracker;
	public ProgressBar progressBar;
	public RedmineIssueListItemForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = (TextView)view.findViewById(R.id.subject);
		textStatus = (TextView)view.findViewById(R.id.status);
		textTicketid = (TextView)view.findViewById(R.id.ticketid);
		textDescription = (TextView)view.findViewById(R.id.description);
		textBottomtext = (TextView)view.findViewById(R.id.bottomtext);
		progressBar = (ProgressBar)view.findViewById(R.id.progressissue);
		textTracker = (TextView)view.findViewById(R.id.textTracker);
	}


	public void setValue(RedmineIssue rd){
		textSubject.setText(rd.getSubject());
		textTicketid.setText("#"+rd.getIssueId().toString());
		textDescription.setText(cutoffString(rd.getDescription(),4));
		progressBar.setMax(100);
		progressBar.setProgress(rd.getProgressRate());
		setMasterName(textBottomtext, rd.getAuthor());
		setMasterName(textStatus, rd.getStatus());
		setMasterName(textTracker, rd.getTracker());

	}

}

