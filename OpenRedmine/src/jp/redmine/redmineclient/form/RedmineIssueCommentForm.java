package jp.redmine.redmineclient.form;

import com.andreabaccega.widget.FormEditText;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.form.helper.FormHelper;

public class RedmineIssueCommentForm extends FormHelper {
	public FormEditText textDescription;
	public Button buttonOK;
	public LinearLayout layoutComment;

	public RedmineIssueCommentForm(Activity issueViewActivity) {

		this.setup(issueViewActivity);
		this.setupEvents();
	}

	protected void setup(Activity view) {
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
		hide();
	}
	public void toggle(){
		if(isVisible()){
			hide();
		} else {
			show();
			textDescription.requestFocus();
		}
	}

	public void show(){
		if(isVisible())
			return;
		layoutComment.setVisibility(View.VISIBLE);
	}

	public void hide(){
		if(!isVisible())
			return;
		layoutComment.setVisibility(View.GONE);
	}

	public boolean isVisible(){
		return layoutComment.getVisibility() == View.VISIBLE;
	}


	@Override
	public boolean Validate() {
		if(!ValidateForm(textDescription))
			return false;
		return super.Validate();
	}

}
