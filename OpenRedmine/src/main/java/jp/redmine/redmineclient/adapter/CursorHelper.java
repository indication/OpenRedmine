package jp.redmine.redmineclient.adapter;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.TypeConverter;

public class CursorHelper {
	private static final String TAG = CursorHelper.class.getSimpleName();

	@SuppressLint("StringFormatMatches")
	static public String convertUserName(View form,Cursor cursor, String name, String loginName){
		return form.getContext().getString(R.string.format_name
				, getCursorString(cursor, name)
				, getCursorString(cursor, loginName)
		);
	}

	static protected String getCursorString(Cursor cursor, String id){
		if(TextUtils.isEmpty(id))
			return "";
		int name_id = cursor.getColumnIndex(id);
		return (name_id > 0) ? cursor.getString(name_id) : "";
	}
	static protected Integer getCursorInt(Cursor cursor, String id, Integer default_value){
		if(TextUtils.isEmpty(id))
			return default_value;
		int name_id = cursor.getColumnIndex(id);
		return (name_id > 0) ? cursor.getInt(name_id) : default_value;
	}
	static public void setText(TextView v,Cursor cursor, String id){
		v.setText(getCursorString(cursor, id));
	}
	static public void setText(TextView v,Cursor cursor, int format_id, String id){
		v.setText(v.getContext().getString(format_id, getCursorString(cursor, id)));
	}
	static public void setText(TextView v,Cursor cursor, String format, String id){
		v.setText(String.format(format, getCursorString(cursor, id)));
	}
	static public void setDate(TextView v,Cursor cursor, String id){
		setDate(v,cursor, id, R.string.format_date);
	}
	static public void setDateTime(TextView v,Cursor cursor, String id){
		setDate(v,cursor, id, R.string.format_datetime);
	}
	static public void setProgress(ProgressBar progressBar, Cursor cursor, String progress1, String progress2) {
		Integer value1 = getCursorInt(cursor, progress1, null);
		Integer value2 = getCursorInt(cursor, progress2, null);
		progressBar.setProgress(value1 == null ? 0 : value1);
		if(value2!= null)
			progressBar.setSecondaryProgress(value2);
	}
	/**
	 * Set ct to v
	 * @param v TextView to set date
	 * @param id the date column id
	 */
	static protected void setDate(TextView v,Cursor cursor, String id, int format_id){
		String date = getCursorString(cursor, id);
		if(TextUtils.isEmpty(date)) {
			v.setText("");
			return;
		}
		Date result = TypeConverter.parseDate(date);
		v.setText(v.getContext().getString(format_id, result));
	}
	static public void setDateTimeSpan(TextView v,Cursor cursor, String id){
		String date = getCursorString(cursor, id);
		if(TextUtils.isEmpty(date)) {
			v.setText("");
			return;
		}
		Date result = TypeConverter.parseDate(date);
		v.setText(DateUtils.getRelativeTimeSpanString(result.getTime()));
	}
}
