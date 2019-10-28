package jp.redmine.redmineclient.form;


import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.EditText;

public class HttpAuthDialogForm extends FormHelper {
	private View view;
	public EditText editAuthID;
	public EditText editAuthPasswd;
	public HttpAuthDialogForm(View activity){
		this.view = activity;
		this.setup();
		this.setupDefaults();
	}


	public void setup(){
		editAuthID = view.findViewById(R.id.editAuthID);
		editAuthPasswd = view.findViewById(R.id.editAuthPasswd);
	}


	public String getUserID(){
		return editAuthID.getText().toString();
	}
	public String getPassword(){
		return editAuthPasswd.getText().toString();
	}

	public void setUserID(String id){
		editAuthID.setText(id);
	}
	public void setPassword(String passwd){
		editAuthPasswd.setText(passwd);
	}
}

