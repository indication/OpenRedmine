package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.TextView;

public class IMasterRecordListItemForm extends FormHelper {
	public TextView textSubject;
	public IMasterRecordListItemForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = (TextView)view.findViewById(android.R.id.text1);
	}


	public void setValue(IMasterRecord rd){
		textSubject.setText(rd.getName());

	}

}

