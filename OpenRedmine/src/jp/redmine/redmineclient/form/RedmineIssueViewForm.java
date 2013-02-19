package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineIssue;
import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class RedmineIssueViewForm extends FormHelper {
	public TextView textIssueId;
	public TextView textSubject;
	public ListView list;
	public View viewHeader;
	public View viewFooter;
	public RedmineIssueViewForm(Activity activity){
		this.setup(activity);

	}


	public void setup(Activity view){
		textIssueId = (TextView)view.findViewById(R.id.textIssueId);
		textSubject = (TextView)view.findViewById(R.id.textSubject);
		list = (ListView)view.findViewById(R.id.list);

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

