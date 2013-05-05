package jp.redmine.redmineclient.form;

import com.andreabaccega.widget.FormEditText;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.RedmineFilterListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineTimeActivityModel;
import jp.redmine.redmineclient.entity.RedmineTimeActivity;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.entity.TypeConverter;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

public class RedmineTimeentryEditForm extends FormHelper {
	public Spinner spinnerActivity;
	public FormEditText textTime;
	public FormEditText textDate;
	public FormEditText textDescription;
	public TableRow rowCreated;
	public TableRow rowModified;
	public TextView textCreated;
	public TextView textModified;
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
		rowCreated = (TableRow)view.findViewById(R.id.rowCreated);
		rowModified = (TableRow)view.findViewById(R.id.rowModified);
		textCreated = (TextView)view.findViewById(R.id.textCreated);
		textModified = (TextView)view.findViewById(R.id.textModified);
		//buttonOK = (Button)view.findViewById(R.id.buttonOK);
		textCreated.setVisibility(View.GONE);
		rowModified.setVisibility(View.GONE);
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
		if(data.getActivity() == null){
			spinnerActivity.setSelection(0);
		} else {
			for(int i = 0; i < adapterActivity.getCount(); i++){
				@SuppressWarnings("deprecation")
				RedmineTimeActivity activity = (RedmineTimeActivity) adapterActivity.getItem(i);
				if(activity.getId() == data.getActivity().getId()){
					spinnerActivity.setSelection(i);
					break;
				}
			}
		}

	}

	public void getValue(RedmineTimeEntry data){
		data.setHours(TextUtils.isEmpty(textTime.getText())? null : TypeConverter.parseBigDecimal(textTime.getText().toString()));
		data.setSpentsOn(getDate(textDate));
		data.setComment(textDescription.getText().toString());
		data.setActivity((RedmineTimeActivity) spinnerActivity.getSelectedItem());
		textCreated.setVisibility(data.getCreated() == null ? View.GONE : View.VISIBLE);
		rowModified.setVisibility(data.getModified() == null ? View.GONE : View.VISIBLE);
		setDateTime(textCreated, data.getCreated());
		setDateTime(textModified, data.getModified());
	}

	public boolean Validate(){
		if(spinnerActivity.getSelectedItem() == null)
			return false;
		if(! (spinnerActivity.getSelectedItem() instanceof RedmineTimeActivity))
			return false;

		return ValidateForms(textDate, textTime);
	}

}

