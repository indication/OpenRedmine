package jp.redmine.redmineclient.adapter.form;

import android.view.View;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.entity.RedmineNews;
import jp.redmine.redmineclient.form.helper.FormHelper;
import jp.redmine.redmineclient.form.helper.TextViewHelper;

public class NewsForm extends FormHelper {
	public TextView textView;
	public TextView textSubject;
	public TextViewHelper textViewHelper;
	public NewsForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textView = view.findViewById(R.id.textView);
		textSubject = view.findViewById(R.id.textSubject);
	}

	public void setupWebView(WebviewActionInterface act){
		textViewHelper = new TextViewHelper();
		textViewHelper.setup(textView);
		textViewHelper.setAction(act);
	}

	public void setValue(RedmineNews jr){
		if (textViewHelper != null)
			textViewHelper.setContent(textView, jr.getConnectionId(), jr.getProject().getId(), jr.getDescription());
		textSubject.setText(jr.getTitle());
	}

}

