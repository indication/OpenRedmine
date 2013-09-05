package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.TextView;

public class RedmineJournalListItemHeaderForm extends FormHelper {
	public TextView textUser;
	public TextView textDate;
	public RedmineJournalListItemHeaderForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textUser = (TextView)view.findViewById(R.id.user);
		textDate = (TextView)view.findViewById(R.id.date);

	}
	public void setValue(RedmineJournal jr){
		setUserName(textUser, jr.getUser());
		setDateTime(textDate,jr.getCreated());
	}
	public void setValue(RedmineIssue jr){
		setUserName(textUser, jr.getAuthor());
		setDateTime(textDate,jr.getCreated());
	}

}

