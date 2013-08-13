package jp.redmine.redmineclient.form;

import android.view.View;
import android.widget.TextView;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.form.helper.FormHelper;

public class StatusUserForm extends FormHelper {
	public TextView textUserName;

	public StatusUserForm(View issueViewActivity) {

		this.setup(issueViewActivity);
		this.setupEvents();
	}

	protected void setup(View view) {
		textUserName = (TextView)view.findViewById(R.id.textUserName);
	}

	public void setValue(RedmineUser rd){
		setMasterName(textUserName, rd);
	}
}
