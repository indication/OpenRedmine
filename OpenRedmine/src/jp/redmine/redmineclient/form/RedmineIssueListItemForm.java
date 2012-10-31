package jp.redmine.redmineclient.form;

import java.sql.Date;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineUser;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RedmineIssueListItemForm extends FormHelper {
	private View view;
	public TextView textSubject;
	public TextView textStatus;
	public TextView textTicketid;
	public TextView textDescription;
	public TextView textBottomtext;
	public ProgressBar progressBar;
	public RedmineIssueListItemForm(View activity){
		this.view = activity;
		this.setup();
	}


	public void setup(){
		textSubject = (TextView)view.findViewById(R.id.subject);
		textStatus = (TextView)view.findViewById(R.id.status);
		textTicketid = (TextView)view.findViewById(R.id.ticketid);
		textDescription = (TextView)view.findViewById(R.id.description);
		textBottomtext = (TextView)view.findViewById(R.id.bottomtext);
		progressBar = (ProgressBar)view.findViewById(R.id.progressissue);
	}


	public void setValue(RedmineIssue rd){
		textSubject.setText(rd.getSubject());
		textTicketid.setText("#"+rd.getIssueId().toString());
		textDescription.setText(cutoffString(rd.getDescription(),4));
		progressBar.setMax(100);
		progressBar.setProgress(rd.getProgressRate());
		textBottomtext.setText(rd.getAuthor() == null ? "" : rd.getAuthor().getName());
		textStatus.setText(rd.getStatus() == null ? "" : rd.getStatus().getName());

	}

	protected String generateBottomText(RedmineUser author,RedmineUser assinged, Date created, Date modified){

		return "";
	}

}

