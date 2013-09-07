package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineIssueRelation;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RedmineRelationListItemForm extends FormHelper {
	public TextView textSubject;
	public TextView textTicketid;
	public TextView textStatus;
	public TextView textDelay;
	public ProgressBar progressBar;
	public RedmineRelationListItemForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = (TextView)view.findViewById(R.id.textSubject);
		textTicketid = (TextView)view.findViewById(R.id.textTicketid);
		textStatus = (TextView)view.findViewById(R.id.textStatus);
		textDelay = (TextView)view.findViewById(R.id.textDelay);
		progressBar = (ProgressBar)view.findViewById(R.id.progressissue);

	}
	public void setValue(RedmineIssueRelation rd){
		if(rd.getType() != null )
			textDelay.setText(textDelay.getContext().getString(rd.getType().getResourceId(),
					(rd.getDelay() == null ? 0 : rd.getDelay().intValue()) ));
		setValue(rd.getIssue() == null ? new RedmineIssue() : rd.getIssue());
	}

	public void setValue(RedmineIssue rd){
		textSubject.setText(rd.getSubject());
		textTicketid.setText(rd.getIssueId() == null ? "" : "#"+rd.getIssueId().toString());
		setMasterName(textStatus, rd.getStatus());
		setProgress(rd.getProgressRate(),rd.getDoneRate());
	}

	public void setProgress(Short progress,Short donerate){
		progressBar.setMax(100);
		progressBar.setProgress(progress == null ? 0 : progress);
		progressBar.setSecondaryProgress(donerate == null ? 0 : donerate);
	}

}

