package jp.redmine.redmineclient.adapter.form;

import android.view.View;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.form.helper.FormHelper;

public class IssueJournalHeaderForm extends FormHelper {
	public TextView textUser;
	public TextView textDate;
	public TextView textNo;
	public IssueJournalHeaderForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textUser = (TextView)view.findViewById(R.id.user);
		textDate = (TextView)view.findViewById(R.id.date);
		textNo = (TextView)view.findViewById(R.id.no);
		textNo.setText("");
	}
	public void setJournalNo(int no){
		textNo.setText(textNo.getContext().getString(R.string.ticket_journal_id, no));
	}
	public void setValue(RedmineJournal jr){
		setUserName(textUser, jr.getUser());
		setDateTimeSpan(textDate,jr.getCreated());
	}
	public void setValue(RedmineIssue jr){
		setUserName(textUser, jr.getAuthor());
		setDateTimeSpan(textDate,jr.getCreated());
	}

}

