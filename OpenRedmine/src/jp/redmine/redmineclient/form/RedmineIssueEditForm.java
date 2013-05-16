package jp.redmine.redmineclient.form;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.andreabaccega.widget.FormEditText;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.RedmineFilterListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineCategoryModel;
import jp.redmine.redmineclient.db.cache.RedminePriorityModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineTimeActivity;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.TypeConverter;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

public class RedmineIssueEditForm extends FormHelper {
	public Spinner spinnerStatus;
	public Spinner spinnerCategory;
	public Spinner spinnerPriority;
	public Spinner spinnerVersion;
	public Spinner spinnerAssigned;
	public SeekBar progressIssue;

	public FormEditText textTitle;
	public FormEditText textDescription;
	public FormEditText textDateStart;
	public FormEditText textDateDue;
	public ImageButton imageCalendarStart;
	public ImageButton imageCalendarDue;
	public TableRow rowCreated;
	public TableRow rowModified;
	public TextView textCreated;
	public TextView textModified;
	public TextView textProgress;
	public Button buttonOK;
	public DatePickerDialog dialogDatePicker;
	RedmineFilterListAdapter adapterStatus;
	RedmineFilterListAdapter adapterCategory;
	RedmineFilterListAdapter adapterPriority;
	RedmineFilterListAdapter adapterUser;
	RedmineFilterListAdapter adapterVersion;
	public RedmineIssueEditForm(Activity activity){
		this.setup(activity);
		this.setupEvents();
	}


	public void setup(Activity view){
		spinnerStatus = (Spinner)view.findViewById(R.id.spinnerStatus);
		spinnerCategory = (Spinner)view.findViewById(R.id.spinnerCategory);
		spinnerPriority = (Spinner)view.findViewById(R.id.spinnerPriority);
		spinnerVersion = (Spinner)view.findViewById(R.id.spinnerVersion);
		spinnerAssigned = (Spinner)view.findViewById(R.id.spinnerAssigned);
		progressIssue = (SeekBar)view.findViewById(R.id.progressIssue);

		textTitle = (FormEditText)view.findViewById(R.id.textTitle);
		textDescription = (FormEditText)view.findViewById(R.id.textDescription);
		textDateStart = (FormEditText)view.findViewById(R.id.textDateStart);
		textDateDue = (FormEditText)view.findViewById(R.id.textDateDue);
		imageCalendarStart = (ImageButton)view.findViewById(R.id.imageCalendarStart);
		imageCalendarDue = (ImageButton)view.findViewById(R.id.imageCalendarDue);

		rowCreated = (TableRow)view.findViewById(R.id.rowCreated);
		rowModified = (TableRow)view.findViewById(R.id.rowModified);
		textCreated = (TextView)view.findViewById(R.id.textCreated);
		textModified = (TextView)view.findViewById(R.id.textModified);
		textProgress = (TextView)view.findViewById(R.id.textProgress);
		textCreated.setVisibility(View.GONE);
		rowModified.setVisibility(View.GONE);
	}

	@Override
	public void setupEvents() {
		setupDateSelector(imageCalendarStart, textDateStart);
		setupDateSelector(imageCalendarDue, textDateDue);

		progressIssue.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				if(fromUser){
					int current = Math.round(((float)progress)/10)*10;
					if(current != seekBar.getProgress()){
						seekBar.setProgress(current);
					}
					progress = current;
				}
				textProgress.setText(textProgress.getContext().getString(R.string.format_progress, progress));
			}
		});
	}

	protected void setupDateSelector(ImageButton button, final FormEditText text){
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar date = Calendar.getInstance();
				if(!TextUtils.isEmpty(text.getText()))
					date.setTime(TypeConverter.parseDate(text.getText().toString()));
				dialogDatePicker = new DatePickerDialog(v.getContext(), new OnDateSetListener() {

					@SuppressLint("SimpleDateFormat")
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
						Calendar selected = Calendar.getInstance();
						selected.set(year, monthOfYear, dayOfMonth);
						SimpleDateFormat format = new SimpleDateFormat();
						format.applyPattern(TypeConverter.FORMAT_DATE);
						text.setText(format.format(selected.getTime()));

					}
				}, date.get(Calendar.YEAR),  date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
				dialogDatePicker.show();
			}
		});

	}

	public void setupDatabase(DatabaseCacheHelper helper){
		adapterStatus = new RedmineFilterListAdapter(new RedmineStatusModel(helper));
		adapterCategory = new RedmineFilterListAdapter(new RedmineCategoryModel(helper));
		adapterPriority = new RedmineFilterListAdapter(new RedminePriorityModel(helper));
		adapterUser = new RedmineFilterListAdapter(new RedmineUserModel(helper));
		adapterVersion = new RedmineFilterListAdapter(new RedmineVersionModel(helper));
	}

	public void setupParameter(int connection, long project){
		setupParameter(spinnerStatus, adapterStatus, connection, project, true);
		setupParameter(spinnerCategory, adapterCategory, connection, project, true);
		setupParameter(spinnerPriority, adapterPriority, connection, project, true);
		setupParameter(spinnerAssigned, adapterUser, connection, project, true);
		setupParameter(spinnerVersion, adapterVersion, connection, project, true);

	}

	protected void setupParameter(Spinner spinner, RedmineFilterListAdapter adapter
			,int connection, long project, boolean isAdd){
		adapter.setupParameter(connection, project, isAdd);
		spinner.setAdapter(adapter);

		adapter.notifyDataSetInvalidated();
		adapter.notifyDataSetChanged();
	}

	public void setValue(RedmineIssue data){
		setDate(textDateStart, data.getDateStart());
		setDate(textDateDue, data.getDateDue());
		textTitle.setText(data.getSubject());
		textDescription.setText(data.getDescription());

		setSpinnerItem(spinnerStatus,adapterStatus,data.getStatus());
		setSpinnerItem(spinnerCategory,adapterCategory,data.getCategory());
		setSpinnerItem(spinnerPriority,adapterPriority,data.getPriority());
		setSpinnerItem(spinnerAssigned,adapterUser,data.getAssigned());
		setSpinnerItem(spinnerVersion,adapterVersion,data.getVersion());

		progressIssue.setProgress(data.getProgressRate());
		textCreated.setVisibility(data.getCreated() == null ? View.GONE : View.VISIBLE);
		rowModified.setVisibility(data.getModified() == null ? View.GONE : View.VISIBLE);
		setDateTime(textCreated, data.getCreated());
		setDateTime(textModified, data.getModified());

	}

	protected void setSpinnerItem(Spinner spinner, RedmineFilterListAdapter adapter, IMasterRecord record){
		if(record == null){
			spinner.setSelection(0);
		} else {
			for(int i = 0; i < adapter.getCount(); i++){
				@SuppressWarnings("deprecation")
				IMasterRecord activity = (IMasterRecord) adapter.getItem(i);
				if(activity.getId() == record.getId()){
					spinner.setSelection(i);
					break;
				}
			}
		}

	}

	public void getValue(RedmineIssue data){

	}

	public boolean Validate(){
		if(spinnerStatus.getSelectedItem() == null || ! (spinnerStatus.getSelectedItem() instanceof RedmineTimeActivity))
			return false;

		if(spinnerCategory.getSelectedItem() == null || ! (spinnerCategory.getSelectedItem() instanceof RedmineProjectCategory))
			return false;

		if(spinnerPriority.getSelectedItem() == null || ! (spinnerPriority.getSelectedItem() instanceof RedminePriority))
			return false;

		if(spinnerAssigned.getSelectedItem() == null || ! (spinnerAssigned.getSelectedItem() instanceof RedmineUser))
			return false;

		return ValidateForms(textDateStart, textDateDue, textTitle, textDescription);
	}

}

