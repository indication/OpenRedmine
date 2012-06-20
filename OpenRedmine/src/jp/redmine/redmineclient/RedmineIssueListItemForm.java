package jp.redmine.redmineclient;

import jp.redmine.redmineclient.entity.RedmineIssue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RedmineIssueListItemForm {
	private View view;
	public TextView textSubject;
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
		textTicketid = (TextView)view.findViewById(R.id.ticketid);
		textDescription = (TextView)view.findViewById(R.id.description);
		textBottomtext = (TextView)view.findViewById(R.id.bottomtext);
		progressBar = (ProgressBar)view.findViewById(R.id.progressissue);
	}

	public void setupEvents(){


	}


	public void setValue(RedmineIssue rd){
		textSubject.setText(rd.getSubject());
		textTicketid.setText("#"+rd.getIssueId().toString());
		textDescription.setText(cutoffString(rd.getDescription(),4));
		progressBar.setMax(100);
		progressBar.setProgress(rd.getProgressRate());

	}

	protected String cutoffString(String str,int cutoff){
		int limit = 0;
		String[] strs = str.split("[\r\n]+",cutoff+1);
		StringBuilder result = new StringBuilder();
		for(String item : strs){
			result.append(item);
			result.append("\r\n");
			limit++;
			if(limit >= cutoff){
				break;
			}
		}
		return result.toString();
	}

}

