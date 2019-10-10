package jp.redmine.redmineclient.adapter.form;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.TextView;

public class ConnectionForm extends FormHelper {
	public TextView textSubject;
	public ConnectionForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = view.findViewById(android.R.id.text1);
	}


	public void setValue(RedmineConnection rd){
		textSubject.setText(rd.getName());

	}

}

