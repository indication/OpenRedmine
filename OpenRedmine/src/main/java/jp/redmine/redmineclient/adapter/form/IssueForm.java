package jp.redmine.redmineclient.adapter.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class IssueForm extends IssueBaseForm {
	public TextView textSubject;
	public TextView textTicketid;
	public TextView textDescription;
	public IssueForm(View activity){
		super(activity);
	}

	@Override
	public void setup(View view){
		super.setup(view);
		textSubject = (TextView)view.findViewById(R.id.textSubject);
		textTicketid = (TextView)view.findViewById(R.id.textTicketid);
		textDescription = (TextView)view.findViewById(R.id.description);
	}

	@Override
	public void setValue(RedmineIssue rd){
		super.setValue(rd);
		textSubject.setText(rd.getSubject());
		textTicketid.setText("#"+rd.getIssueId().toString());
		textDescription.setText(rd.getDescription());

		boolean isEnabled = true;
		if(rd.getStatus() != null && rd.getStatus().isIs_close()){
			isEnabled = false;
		}
		performSetEnabled((ViewGroup)(textSubject.getParent()), isEnabled);

	}

}

