package jp.redmine.redmineclient.form;

import java.util.Date;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RedmineIssueViewForm extends FormHelper {
	private Activity view;
	public TextView textTracker;
	public TextView textCategory;
	public TextView textPrivate;
	public TextView textIsueId;
	public TextView textAuthor;
	public TextView textSubject;
	public TextView textStatus;
	public TextView textProgress;
	public TextView textPriority;
	public TextView textAssignedTo;
	public TextView textDateFrom;
	public TextView textDateTo;
	public TextView textVersion;
	public TextView textDescription;
	public ProgressBar progressBar;
	public LinearLayout layoutTitleContent;
	public ViewGroup layoutTitle1;
	public ViewGroup layoutTitle2;
	public RedmineIssueViewForm(Activity activity){
		this.view = activity;
		this.setup();
		this.setupEvents();
	}


	public void setup(){
		textTracker = (TextView)view.findViewById(R.id.textTracker);
		textCategory = (TextView)view.findViewById(R.id.textCategory);
		textPrivate = (TextView)view.findViewById(R.id.textPrivate);
		textPriority = (TextView)view.findViewById(R.id.textPriority);
		textIsueId = (TextView)view.findViewById(R.id.textIsueId);
		textAuthor = (TextView)view.findViewById(R.id.textAuthor);
		textSubject = (TextView)view.findViewById(R.id.textSubject);
		textStatus = (TextView)view.findViewById(R.id.textStatus);
		textProgress = (TextView)view.findViewById(R.id.textProgress);
		textAssignedTo = (TextView)view.findViewById(R.id.textAssignedTo);
		textDateFrom = (TextView)view.findViewById(R.id.textDateFrom);
		textDateTo = (TextView)view.findViewById(R.id.textDateTo);
		textVersion = (TextView)view.findViewById(R.id.textVersion);
		textDescription = (TextView)view.findViewById(R.id.textDescription);
		progressBar = (ProgressBar)view.findViewById(R.id.progressissue);
		layoutTitleContent = (LinearLayout)view.findViewById(R.id.layoutTitleContent);
		layoutTitle1 = (ViewGroup)view.findViewById(R.id.layoutTitle1);
		layoutTitle2 = (ViewGroup)view.findViewById(R.id.layoutTitle2);

	}

	public void setupEvents(){
		textSubject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(layoutTitle1.getVisibility() == View.VISIBLE){
					layoutTitle1.setVisibility(View.GONE);
					layoutTitle1.removeAllViews();
					layoutTitle2.addView(layoutTitleContent);
					layoutTitle2.setVisibility(View.VISIBLE);
				} else {
					layoutTitle2.setVisibility(View.GONE);
					layoutTitle2.removeAllViews();
					layoutTitle1.addView(layoutTitleContent);
					layoutTitle1.setVisibility(View.VISIBLE);
				}

			}
		});
	}

	public void setIssueId(int id){
		textIsueId.setText("#"+String.valueOf(id));
	}

	protected String convertUserName(RedmineUser us){
		if(us == null)
			return "";
		return view.getString(R.string.format_name, us.getName(), us.getLoginName());
	}

	protected String convertDate(Date date){
		if(date == null)
			return "";
		return view.getString(R.string.format_date, date);
	}

	public void setTracker(RedmineTracker tk){
		textTracker.setText(tk == null ? "" : tk.getName());
	}
	public void setCategory(RedmineProjectCategory ct){
		textCategory.setText(ct == null ? "" : ct.getName());
	}
	public void setVersion(RedmineProjectVersion vr){
		textVersion.setText(vr == null ? "" : vr.getName());
	}
	public void setPrivate(boolean isPrivate){
		performSetVisible(textPrivate, isPrivate);
	}
	public void setStatus(RedmineStatus status){
		textStatus.setText(status == null ? "" : status.getName());
	}
	public void setPriority(RedminePriority pr){
		textPriority.setText(pr == null ? "" : pr.getName());
	}

	protected void setUserName(TextView v,RedmineUser ct){
		v.setText(convertUserName(ct));
	}

	public void setProgress(short progress,short donerate){
		progressBar.setMax(100);
		progressBar.setProgress(progress);
		progressBar.setSecondaryProgress(donerate);
		textProgress.setText(String.valueOf(donerate).concat("%"));
	}

	public void setValue(RedmineIssue rd){
		textSubject.setText(rd.getSubject());
		textDescription.setText(rd.getDescription());
		textDateFrom.setText(convertDate(rd.getDateStart()));
		textDateTo.setText(convertDate(rd.getDateDue()));
		setTracker(rd.getTracker());
		setIssueId(rd.getIssueId());
		setUserName(textAuthor,rd.getAuthor());
		setUserName(textAssignedTo,rd.getAssigned());
		setStatus(rd.getStatus());
		setPriority(rd.getPriority());
		setPrivate(rd.isPrivate());
		setCategory(rd.getCategory());
		setVersion(rd.getVersion());
		setProgress(rd.getProgressRate(),rd.getDoneRate());

	}

}

