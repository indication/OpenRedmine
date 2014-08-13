package jp.redmine.redmineclient.fragment.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IssueViewForm extends FormHelper {
	public TextView textIssueId;
	public TextView textSubject;
	public LinearLayout layoutTitleContent;
	public ViewGroup layoutTitle1;
	public ViewGroup layoutTitle2;
	public IssueViewForm(View activity){
		this.setup(activity);
		this.setupEvents();
	}


	public void setup(View view){
		textIssueId = (TextView)view.findViewById(R.id.textIssueId);
		textSubject = (TextView)view.findViewById(R.id.textSubject);
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

	public void setIssueId(Integer id){
		textIssueId.setText(id == null ? "" : "#"+String.valueOf(id));
	}


	public void setValue(RedmineIssue rd){
		textSubject.setText(rd.getSubject());
		setIssueId(rd.getIssueId());

	}


}

