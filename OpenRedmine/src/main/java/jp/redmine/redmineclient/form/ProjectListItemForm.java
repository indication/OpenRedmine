package jp.redmine.redmineclient.form;

import android.view.View;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.helper.FormHelper;

public class ProjectListItemForm extends FormHelper {
	public TextView textSubject;
    public CheckBox ratingBar;
	public ProjectListItemForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = (TextView)view.findViewById(R.id.textSubject);
		ratingBar = (CheckBox)view.findViewById(R.id.checkStar);
	}


	public void setValue(RedmineProject rd){
		textSubject.setText(rd.getName());
		ratingBar.setChecked(rd.getFavorite() != null && rd.getFavorite() > 0 ? true : false);

	}
	public void getValue(RedmineProject rd){
		rd.setFavorite(ratingBar.isChecked() ? 1 : 0 );
	}

}

