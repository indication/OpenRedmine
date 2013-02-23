package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RedmineIssueViewForm extends FormHelper {
	public TextView textIssueId;
	public TextView textSubject;
	public ListView list;
	public View viewHeader;
	public View viewFooter;
	public LinearLayout layoutTitleContent;
	public ViewGroup layoutTitle1;
	public ViewGroup layoutTitle2;
	public RedmineIssueViewForm(Activity activity){
		this.setup(activity);

		this.setupEvents();
	}


	public void setup(Activity view){
		textIssueId = (TextView)view.findViewById(R.id.textIssueId);
		textSubject = (TextView)view.findViewById(R.id.textSubject);
		list = (ListView)view.findViewById(R.id.list);
		layoutTitleContent = (LinearLayout)view.findViewById(R.id.layoutTitleContent);
		layoutTitle1 = (ViewGroup)view.findViewById(R.id.layoutTitle1);
		layoutTitle2 = (ViewGroup)view.findViewById(R.id.layoutTitle2);

		viewHeader = view.getLayoutInflater().inflate(R.layout.issueviewdetail,null);
		viewFooter = view.getLayoutInflater().inflate(R.layout.listview_footer,null);
		list.addHeaderView(viewHeader);
		setListHeaderViewVisible(false);
		list.addFooterView(viewFooter);
		setListFooterViewVisible(false);
	}

	public void setListHeaderViewVisible(boolean isVisible){
		viewHeader.setVisibility(isVisible ? View.VISIBLE : View.GONE);
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

	public void setListFooterViewVisible(boolean isVisible){
		viewFooter.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}


	public void setIssueId(int id){
		textIssueId.setText("#"+String.valueOf(id));
	}


	public void setValue(RedmineIssue rd){
		textSubject.setText(rd.getSubject());
		setIssueId(rd.getIssueId());

	}


}

