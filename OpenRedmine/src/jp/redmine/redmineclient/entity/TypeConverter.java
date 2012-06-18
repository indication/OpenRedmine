package jp.redmine.redmineclient.entity;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.util.Log;


public class TypeConverter {
	//Sat Sep 29 12:03:04 +0200 2007
	//E   M   d  H :m :s  Z     y
	//public static final String FORMAT_DATETIME = "E M d H:m:s Z y";
	public static final String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ssZ";
	public static final String FORMAT_DATE = "yyyy-MM-dd";
	public static Date parseDate(String datetime){
		return parseDateTimeFormat(datetime,FORMAT_DATE);
	}
	public static Date parseDateTime(String datetime){
		return parseDateTimeFormat(datetime,FORMAT_DATETIME);
	}
	public static Date parseDateTimeFormat(String datetime,String formatstr){
		SimpleDateFormat format = new SimpleDateFormat(formatstr);
		if("".equals(datetime)) return null;
		Date item = new Date();
		try {
			item = format.parse(datetime);
		} catch (ParseException e) {
			Log.e("TypeConverter","ParseDate",e);
		}
		return item;
	}
}
