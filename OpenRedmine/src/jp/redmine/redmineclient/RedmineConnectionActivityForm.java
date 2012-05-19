package jp.redmine.redmineclient;

import jp.redmine.redmineclient.entity.RedmineConnection;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

public class RedmineConnectionActivityForm {
	private Activity activity;
	public EditText editName;
	public EditText editUrl;
	public EditText editToken;
	public EditText editAuthID;
	public EditText editAuthPasswd;
	public Button buttonSave;
	public CheckBox checkHttpAuth;
	public LinearLayout formHttpAuth;
	public RedmineConnectionActivityForm(Activity activity){
		this.activity = activity;
		this.setup();
	}


	public void setup(){
		editName = (EditText)activity.findViewById(R.id.editName);
		editUrl = (EditText)activity.findViewById(R.id.editURL);
		editToken = (EditText)activity.findViewById(R.id.editToken);
		editAuthID = (EditText)activity.findViewById(R.id.editAuthID);
		editAuthPasswd = (EditText)activity.findViewById(R.id.editAuthPasswd);
		buttonSave = (Button)activity.findViewById(R.id.buttonSave);
		formHttpAuth = (LinearLayout)activity.findViewById(R.id.formHttpAuth);
		checkHttpAuth = (CheckBox)activity.findViewById(R.id.checkHttpAuth);

	}

	public void setupEvents(){
		checkHttpAuth.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
				//formHttpAuth.setEnabled(flag);
				//editAuthID.setEnabled(flag);
				//editAuthPasswd.setEnabled(flag);
				for(int idx = 0; idx < formHttpAuth.getChildCount(); idx++){
					View item = formHttpAuth.getChildAt(idx);
					item.setEnabled(flag);
					//item.setFocusable(flag);
					if( !flag && item.isFocused() ){
						//@todo have to lost forcus on here!!!
					}
				}
			}
		});

	}

	public void setValue(RedmineConnection rd){

		editName.setText(rd.Name());
		editUrl.setText(rd.Url());
		editToken.setText(rd.Token());
		checkHttpAuth.setChecked(rd.Auth());
		editAuthID.setText(rd.AuthId());
		editAuthPasswd.setText(rd.AuthPasswd());
		formHttpAuth.setEnabled(rd.Auth());
	}
	public void getValue(RedmineConnection rd){

		rd.Name(editName.getText().toString());
		rd.Url(editUrl.getText().toString());
		rd.Token(editToken.getText().toString());
		rd.Auth(checkHttpAuth.isChecked());
		rd.AuthId(editAuthID.getText().toString());
		rd.AuthPasswd(editAuthPasswd.getText().toString());
	}
}

