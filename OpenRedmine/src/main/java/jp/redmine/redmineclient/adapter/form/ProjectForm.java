package jp.redmine.redmineclient.adapter.form;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.helper.FormHelper;

public class ProjectForm extends FormHelper {
	public TextView textSubject;
    public CheckBox ratingBar;
	public ProjectForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = view.findViewById(R.id.textSubject);
		ratingBar = view.findViewById(R.id.checkStar);
		ratingBar.setFocusable(false);
	}


	public void setValue(RedmineProject rd){
		performSetEnabled(textSubject, rd.getStatus().isUpdateable());
		textSubject.setText(rd.getName());
		ratingBar.setChecked(rd.getFavorite() > 0);

	}
	public void getValue(RedmineProject rd){
		rd.setFavorite(ratingBar.isChecked() ? 1 : 0 );
	}

}

