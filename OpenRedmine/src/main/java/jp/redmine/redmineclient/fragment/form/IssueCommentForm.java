package jp.redmine.redmineclient.fragment.form;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.andreabaccega.widget.FormEditText;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.form.helper.FormHelper;

public class IssueCommentForm extends FormHelper {
	public FormEditText textDescription;
	public Button buttonOK;
	public LinearLayout layoutComment;

	public IssueCommentForm(View issueViewActivity) {

		this.setup(issueViewActivity);
		this.setupEvents();
	}

	protected void setup(View view) {
		textDescription = (FormEditText)view.findViewById(R.id.textDescription);
		buttonOK = (Button)view.findViewById(R.id.buttonOK);
		layoutComment = (LinearLayout)view.findViewById(R.id.layoutComment);
	}

	public void getValue(RedmineJournal journal) {
		journal.setNotes(textDescription.getText().toString());
	}

	public void clear() {
		textDescription.setError(null);
		textDescription.setText("");
	}

	public void hide(){
		performSetVisible(layoutComment, false);
	}

	public void show(){
		performSetVisible(layoutComment, true);
	}

	@Override
	public boolean Validate() {
		if(!ValidateForm(textDescription))
			return false;
		return super.Validate();
	}

}
