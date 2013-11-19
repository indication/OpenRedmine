package jp.redmine.redmineclient.form;

import com.andreabaccega.widget.FormEditText;

import android.view.View;
import android.widget.Button;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.form.helper.FormHelper;

public class RedmineIssueCommentForm extends FormHelper {
	public FormEditText textDescription;
	public Button buttonOK;

	public RedmineIssueCommentForm(View issueViewActivity) {

		this.setup(issueViewActivity);
		this.setupEvents();
	}

	protected void setup(View view) {
		textDescription = (FormEditText)view.findViewById(R.id.textDescription);
		buttonOK = (Button)view.findViewById(R.id.buttonOK);
	}

	public void getValue(RedmineJournal journal) {
		journal.setNotes(textDescription.getText().toString());
	}

	public void clear() {
		textDescription.setError(null);
		textDescription.setText("");
	}


	@Override
	public boolean Validate() {
		if(!ValidateForm(textDescription))
			return false;
		return super.Validate();
	}

}
