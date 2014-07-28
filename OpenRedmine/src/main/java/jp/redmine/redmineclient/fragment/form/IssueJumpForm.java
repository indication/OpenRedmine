package jp.redmine.redmineclient.fragment.form;

import com.andreabaccega.widget.FormEditText;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.TypeConverter;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class IssueJumpForm extends FormHelper {
	public FormEditText textIssueId;
	public Button buttonOK;
	public Button buttonClear;
	public IssueJumpForm(View activity){
		this.setup(activity);
		this.setupEvents();
	}


	public void setup(View view){
		textIssueId = (FormEditText)view.findViewById(R.id.textIssueId);
		buttonOK = (Button)view.findViewById(R.id.buttonOK);
		buttonClear = (Button)view.findViewById(R.id.buttonC);
	}

	public void setupEvents(){
		buttonClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				textIssueId.setText("");
			}
		});
		textIssueId.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_GO) {
		        	buttonOK.performClick();
		            return true;
		        }
		        return false;
		    }

		});
	}

	public boolean Validate(){
		return ValidateForms(textIssueId);
	}

	public int getIssueId(){
		return TypeConverter.parseInteger(textIssueId.getText().toString());
	}

}

