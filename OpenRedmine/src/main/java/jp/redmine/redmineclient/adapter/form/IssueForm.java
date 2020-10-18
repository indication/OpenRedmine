package jp.redmine.redmineclient.adapter.form;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineRecentIssue;

public class IssueForm extends IssueBaseForm {
	public TextView textSubject;
	public TextView textTicketid;
	public TextView textDescription;
	public ImageView imageRecent;
	public IssueForm(View activity){
		super(activity);
	}

	@Override
	public void setup(View view){
		super.setup(view);
		textSubject = view.findViewById(R.id.textSubject);
		textTicketid = view.findViewById(R.id.textTicketid);
		textDescription = view.findViewById(R.id.description);
		imageRecent = view.findViewById(R.id.imageRecent);
	}

	public void setValue(RedmineRecentIssue recent){
		genericSetValue(recent.getIssue());
		setDateTimeSpan(textModified, recent.getModified());
		imageRecent.setVisibility(View.VISIBLE);
	}

	@Override
	public void setValue(RedmineIssue rd){
		genericSetValue(rd);
		setDateTimeSpan(textModified, rd.getModified());
		imageRecent.setVisibility(View.GONE);
	}

	protected void genericSetValue(RedmineIssue rd){
		super.setValue(rd);
		textSubject.setText(rd.getSubject());
		textTicketid.setText("#"+rd.getIssueId().toString());
		textDescription.setText(rd.getDescription());

		boolean isEnabled = true;
		if(rd.getStatus() != null && rd.getStatus().isClose()){
			isEnabled = false;
		}
		performSetEnabled((ViewGroup)(textSubject.getParent()), isEnabled);

	}

}

