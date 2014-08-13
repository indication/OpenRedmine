package jp.redmine.redmineclient.adapter.form;

import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.TextView;

public class IMasterRecordForm extends FormHelper {
	public TextView textSubject;
	public IMasterRecordForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = (TextView)view.findViewById(android.R.id.text1);
	}


	public void setValue(IMasterRecord rd){
		textSubject.setText(rd.getName());

	}

}

