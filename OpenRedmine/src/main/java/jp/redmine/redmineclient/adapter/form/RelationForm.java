package jp.redmine.redmineclient.adapter.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineIssueRelation;
import jp.redmine.redmineclient.entity.RedmineIssueRelation.RelationType;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RelationForm extends FormHelper {
	public TextView textSubject;
	public TextView textTicketid;
	public TextView textStatus;
	public TextView textDelay;
	public ProgressBar progressBar;
	public ImageView imageIcon;
	public RelationForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = view.findViewById(R.id.textSubject);
		textTicketid = view.findViewById(R.id.textTicketid);
		textStatus = view.findViewById(R.id.textStatus);
		textDelay = view.findViewById(R.id.textDelay);
		progressBar = view.findViewById(R.id.progressissue);
		imageIcon = view.findViewById(R.id.icon);

	}
	public void setValue(RedmineIssueRelation rd){
		RelationType type = rd.getType() == null ? RelationType.None : rd.getType();
		textDelay.setText(textDelay.getContext().getString(type.getResourceId(),
				(rd.getDelay() == null ? 0 : rd.getDelay().intValue()) ));
		setValue(rd.getIssue() == null ? new RedmineIssue() : rd.getIssue());
		int drawable;
		switch(type){
		case Blocks:
			drawable = R.drawable.ic_relative_base_to;
			break;
		case Blocked:
			drawable = R.drawable.ic_relative_base_from;
			break;
		case Duplicates:
			drawable = R.drawable.ic_relative_duplicate_to;
			break;
		case Duplicated:
			drawable = R.drawable.ic_relative_duplicate_from;
			break;
		case Precedes:
			drawable = R.drawable.ic_relative_precedes_from;
			break;
		case Follows:
			drawable = R.drawable.ic_relative_follows_from;
			break;
		case Copied:
			drawable = R.drawable.ic_relative_copy_to;
			break;
		case Relates:
		default:
			drawable = R.drawable.ic_relative_related;
			break;
		}
		imageIcon.setImageDrawable(imageIcon.getContext().getResources().getDrawable(drawable));
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

