package jp.redmine.redmineclient.form;

import com.andreabaccega.widget.FormEditText;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineConnection;
import android.app.Activity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

public class RedmineConnectionActivityForm extends FormHelper {
	private Activity activity;
	public FormEditText editName;
	public FormEditText editUrl;
	public FormEditText editToken;
	public EditText editAuthID;
	public EditText editAuthPasswd;
	public Button buttonSave;
	public CheckBox checkHttpAuth;
	public LinearLayout formHttpAuth;
	public LinearLayout formPermitUnsafe;
	public CheckBox checkUnsafeConnection;
	public EditText editCertKey;
	public Button buttonAccess;
	public RedmineConnectionActivityForm(Activity activity){
		this.activity = activity;
		this.setup();
		this.setupDefaults();
	}


	public void setup(){
		editName = (FormEditText)activity.findViewById(R.id.editName);
		editUrl = (FormEditText)activity.findViewById(R.id.editURL);
		editToken = (FormEditText)activity.findViewById(R.id.editToken);
		editAuthID = (EditText)activity.findViewById(R.id.editAuthID);
		editAuthPasswd = (EditText)activity.findViewById(R.id.editAuthPasswd);
		buttonSave = (Button)activity.findViewById(R.id.buttonSave);
		buttonAccess = (Button)activity.findViewById(R.id.buttonAccess);
		formHttpAuth = (LinearLayout)activity.findViewById(R.id.formHttpAuth);
		checkHttpAuth = (CheckBox)activity.findViewById(R.id.checkHttpAuth);
		formPermitUnsafe = (LinearLayout)activity.findViewById(R.id.formPermitUnsafe);
		checkUnsafeConnection = (CheckBox)activity.findViewById(R.id.checkPermitUnsafe);
		editCertKey = (EditText)activity.findViewById(R.id.editCertKey);
	}

	public void setupEvents(){
		checkHttpAuth.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
				performSetEnabled(formHttpAuth,flag);
			}
		});
		checkUnsafeConnection.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
				performSetEnabled(formPermitUnsafe,flag);
			}
		});

	}

	public void setupDefaults(){
		performSetEnabled(formHttpAuth,checkHttpAuth.isChecked());
		performSetEnabled(formPermitUnsafe,checkUnsafeConnection.isChecked());
	}


	public boolean Validate(){
		return ValidateForms(
				editName
				,editUrl
				,editToken
				);
	}

	public String getUrl(){
		if(ValidateForm(editUrl)){
			return editUrl.getText().toString();
		} else {
			return "";
		}
	}
	public String getAuthID(){
		return checkHttpAuth.isChecked() ? editAuthID.getText().toString() : "";
	}
	public String getAuthPassword(){
		return checkHttpAuth.isChecked() ? editAuthPasswd.getText().toString() : "";
	}
	public void setUnsafeConnection(boolean flag){
		checkUnsafeConnection.setChecked(flag);
		performSetEnabled(formPermitUnsafe,flag);
	}
	public boolean isUnsafeConnection(){
		return checkUnsafeConnection.isChecked();
	}
	public void setAuthentication(boolean flag){
		checkHttpAuth.setChecked(flag);
		performSetEnabled(formHttpAuth,flag);
	}
	public void setAuthentication(String id, String passwd){
		boolean flag = !("".equals(id) && "".equals(passwd));
		setAuthentication(flag);
		editAuthID.setText(id);
		editAuthPasswd.setText(passwd);
	}
	public void setToken(String token){
		editToken.setText(token);
	}
	public String getToken(){
		return editToken.getText().toString();
	}

	public void setValue(RedmineConnection rd){

		editName.setText(rd.getName());
		editUrl.setText(rd.getUrl());
		editToken.setText(rd.getToken());
		checkHttpAuth.setChecked(rd.isAuth());
		editAuthID.setText(rd.getAuthId());
		editAuthPasswd.setText(rd.getAuthPasswd());
		checkUnsafeConnection.setChecked(rd.isPermitUnsafe());
		editCertKey.setText(rd.getCertKey());
		setupDefaults();
	}
	public void getValue(RedmineConnection rd){

		rd.setName(editName.getText().toString());
		rd.setUrl(editUrl.getText().toString());
		rd.setToken(editToken.getText().toString());
		rd.setAuth(checkHttpAuth.isChecked());
		rd.setAuthId(editAuthID.getText().toString());
		rd.setAuthPasswd(editAuthPasswd.getText().toString());
		rd.setPermitUnsafe(checkUnsafeConnection.isChecked());
		rd.setCertKey(editCertKey.getText().toString());
	}
}

