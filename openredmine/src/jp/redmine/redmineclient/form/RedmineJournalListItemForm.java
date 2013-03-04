package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineJournal;
import android.view.View;
import android.widget.TextView;

public class RedmineJournalListItemForm extends FormHelper {
	private View view;
	public TextView textDescription;
	public TextView textUser;
	public TextView textDate;
	public RedmineJournalListItemForm(View activity){
		this.view = activity;
		this.setup();
	}


	public void setup(){
		textDescription = (TextView)view.findViewById(R.id.description);
		textUser = (TextView)view.findViewById(R.id.user);
		textDate = (TextView)view.findViewById(R.id.date);
	}


	public void setValue(RedmineJournal jr){
		textDescription.setText(jr.getNotes());
		setUserName(textUser, jr.getUser());
		setDateTime(textDate,jr.getCreated());
	}


}

