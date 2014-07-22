package jp.redmine.redmineclient.fragment.form;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.andreabaccega.widget.FormEditText;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.FilterListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineTimeActivityModel;
import jp.redmine.redmineclient.entity.RedmineTimeActivity;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.entity.TypeConverter;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

public class TimeEntryEditForm extends FormHelper {
	public Spinner spinnerActivity;
	public FormEditText textTime;
	public FormEditText textDate;
	public FormEditText textDescription;
	public TableRow rowCreated;
	public TableRow rowModified;
	public TextView textCreated;
	public TextView textModified;
	public ImageButton imageCalendar;
	public Button buttonOK;
	public DatePickerDialog dialogDatePicker;
	FilterListAdapter adapterActivity;
	public TimeEntryEditForm(View view){
		this.setup(view);
		this.setupEvents();
	}


	public void setup(View view){
		spinnerActivity = (Spinner)view.findViewById(R.id.spinnerActivity);
		textTime = (FormEditText)view.findViewById(R.id.textTime);
		textDate = (FormEditText)view.findViewById(R.id.textDate);
		textDescription = (FormEditText)view.findViewById(R.id.textDescription);
		rowCreated = (TableRow)view.findViewById(R.id.rowCreated);
		rowModified = (TableRow)view.findViewById(R.id.rowModified);
		textCreated = (TextView)view.findViewById(R.id.textCreated);
		textModified = (TextView)view.findViewById(R.id.textModified);
		imageCalendar = (ImageButton)view.findViewById(R.id.imageCalendar);
		//buttonOK = (Button)view.findViewById(R.id.buttonOK);
		textCreated.setVisibility(View.GONE);
		rowModified.setVisibility(View.GONE);
	}

	@Override
	public void setupEvents() {
		imageCalendar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar date = Calendar.getInstance();
				if(!TextUtils.isEmpty(textDate.getText()))
					date.setTime(TypeConverter.parseDate(textDate.getText().toString()));
				if(dialogDatePicker == null){
					dialogDatePicker = new DatePickerDialog(v.getContext(), new OnDateSetListener() {

						@SuppressLint("SimpleDateFormat")
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
							Calendar selected = Calendar.getInstance();
							selected.set(year, monthOfYear, dayOfMonth);
							SimpleDateFormat format = new SimpleDateFormat();
							format.applyPattern(TypeConverter.FORMAT_DATE);
							textDate.setText(format.format(selected.getTime()));

						}
					}, date.get(Calendar.YEAR),  date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
				}
				dialogDatePicker.show();
			}
		});
	}

	public void setupDatabase(DatabaseCacheHelper helper){
		adapterActivity = new FilterListAdapter(new RedmineTimeActivityModel(helper));
	}

	public void setupParameter(int connection, long project){

		adapterActivity.setupParameter(connection, project, false);
		spinnerActivity.setAdapter(adapterActivity);
		adapterActivity.notifyDataSetInvalidated();
		adapterActivity.notifyDataSetChanged();
	}

	public void setValue(RedmineTimeEntry data){
		textTime.setText(data.getHours() == null ? "" : String.valueOf(data.getHours()));
		setDate(textDate, data.getSpentsOn());
		textDescription.setText(data.getComment());
		if(data.getActivity() == null){
			if(adapterActivity.getCount()>0)
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
		textCreated.setVisibility(data.getCreated() == null ? View.GONE : View.VISIBLE);
		rowModified.setVisibility(data.getModified() == null ? View.GONE : View.VISIBLE);
		setDateTime(textCreated, data.getCreated());
		setDateTime(textModified, data.getModified());

	}

	public void getValue(RedmineTimeEntry data){
		data.setHours(TextUtils.isEmpty(textTime.getText())? null : TypeConverter.parseBigDecimal(textTime.getText().toString()));
		data.setSpentsOn(getDate(textDate));
		data.setComment(textDescription.getText().toString());
		data.setActivity((RedmineTimeActivity) spinnerActivity.getSelectedItem());
	}

	@Override
	public boolean Validate(){
		if(spinnerActivity.getSelectedItem() == null || ! (spinnerActivity.getSelectedItem() instanceof RedmineTimeActivity)){

			return false;
		}

		return ValidateForms(textDate, textTime);
	}

}

