package jp.redmine.redmineclient.adapter.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.TextView;

public class TimeEntryForm extends FormHelper {
	public TextView textAuthor;
	public TextView textActivity;
	public TextView textSpentsOn;
	public TextView textTimeEntry;
	public TimeEntryForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textAuthor = view.findViewById(R.id.textAuthor);
		textActivity = view.findViewById(R.id.textActivity);
		textSpentsOn = view.findViewById(R.id.textSpentsOn);
		textTimeEntry = view.findViewById(R.id.textTimeEntry);
	}


	public void setValue(RedmineTimeEntry rd){
		setUserName(textAuthor, rd.getUser());
		setMasterName(textActivity, rd.getActivity());
		setDate(textSpentsOn,rd.getSpentsOn());
		setTime(textTimeEntry,R.string.ticket_time_estimate,rd.getHours().doubleValue());

	}


}

