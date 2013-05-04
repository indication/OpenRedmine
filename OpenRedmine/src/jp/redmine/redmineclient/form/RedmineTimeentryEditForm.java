package jp.redmine.redmineclient.form;

import com.andreabaccega.widget.FormEditText;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.RedmineFilterListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineTimeActivityModel;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.app.Activity;
import android.widget.Button;
import android.widget.Spinner;

public class RedmineTimeentryEditForm extends FormHelper {
	public Spinner spinnerActivity;
	public FormEditText textTime;
	public FormEditText textDate;
	public FormEditText textDescription;
	public Button buttonOK;
	RedmineFilterListAdapter adapterActivity;
	public RedmineTimeentryEditForm(Activity activity){
		this.setup(activity);
		this.setupEvents();
	}


	public void setup(Activity view){
		spinnerActivity = (Spinner)view.findViewById(R.id.spinnerActivity);
		textTime = (FormEditText)view.findViewById(R.id.textTime);
		textDate = (FormEditText)view.findViewById(R.id.textDate);
		textDescription = (FormEditText)view.findViewById(R.id.textDescription);
		buttonOK = (Button)view.findViewById(R.id.buttonOK);
	}
	public void setupDatabase(DatabaseCacheHelper helper){
		adapterActivity = new RedmineFilterListAdapter(new RedmineTimeActivityModel(helper));
	}

	public void setupParameter(int connection, long project){

		adapterActivity.setupParameter(connection, project);
		spinnerActivity.setAdapter(adapterActivity);
		adapterActivity.notifyDataSetInvalidated();
		adapterActivity.notifyDataSetChanged();
	}

	public void setValue(RedmineTimeEntry data){
		textTime.setText(data.getHours() == null ? "" : String.valueOf(data.getHours()));
		convertDate(textDate, data.getSpentsOn());
		textDescription.setText(data.getComment());

	}

	public void getValue(RedmineTimeEntry data){

	}

}

