package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.TextView;

public class RedmineProjectListItemForm extends FormHelper {
	public TextView textSubject;
	public RedmineProjectListItemForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = (TextView)view.findViewById(android.R.id.text1);
	}


	public void setValue(RedmineProject rd){
		textSubject.setText(rd.getName());

	}

}

