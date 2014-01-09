package jp.redmine.redmineclient.form;

import android.view.View;
import android.widget.TextView;

import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.RedmineWiki;
import jp.redmine.redmineclient.form.helper.FormHelper;

public class RedmineWikiListItemForm extends FormHelper {
	public TextView textSubject;
	public RedmineWikiListItemForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = (TextView)view.findViewById(android.R.id.text1);
	}


	public void setValue(RedmineWiki rd){
		textSubject.setText(rd.getTitle());

	}

}

