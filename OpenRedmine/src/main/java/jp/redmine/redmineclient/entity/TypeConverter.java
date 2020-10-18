package jp.redmine.redmineclient.entity;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class TypeConverter {
	//Sat Sep 29 12:03:04 +0200 2007
	//E   M   d  H :m :s  Z     y
	//public static final String FORMAT_DATETIME = "E M d H:m:s Z y";
	//2015-02-05 00:38:50 UTC
	public static final String FORMAT_DATETIMEZ = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'";
	public static final String FORMAT_DATETIMESPZ = "yyyy'-'MM'-'dd' 'HH':'mm':'ss Z";
	public static final String FORMAT_DATETIME = "yyyy'-'MM'-'dd'T'HH':'mm':'ssZ";
	public static final String FORMAT_DATETIMEMS = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSSZ";
	public static final String FORMAT_DATE = "yyyy-MM-dd";
	public static Date parseDate(String datetime){
		return parseDateTimeFormat(datetime,FORMAT_DATE, false);
	}
	public static Date parseDateTime(String datetime){
		if (TextUtils.isEmpty(datetime))
			return null;
		if (datetime.contains(" "))
			return parseDateTimeFormat(datetime,FORMAT_DATETIMESPZ, false);
		else if (datetime.endsWith("Z"))
			return parseDateTimeFormat(datetime,FORMAT_DATETIMEZ, true);
		else if(datetime.contains("."))
			return parseDateTimeFormat(datetime,FORMAT_DATETIMEMS, false);
		else
			return parseDateTimeFormat(datetime,FORMAT_DATETIME, false);
	}
	@SuppressLint("SimpleDateFormat")
	public static Date parseDateTimeFormat(String datetime,String formatstr, boolean isSetupUtcTimezone){
		SimpleDateFormat format = new SimpleDateFormat(formatstr);
		if(isSetupUtcTimezone)
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
		if(TextUtils.isEmpty(datetime)) return null;
		Date item = null;
		try {
			item = format.parse(datetime);
		} catch (ParseException e) {
			Log.e("TypeConverter","ParseDate",e);
		}
		return item;
	}
	public static BigDecimal parseBigDecimal(String str){
		if(TextUtils.isEmpty(str)) return null;
		return new BigDecimal(str);
	}
	public static Integer parseInteger(String str){
		if(TextUtils.isEmpty(str)) return null;
		try {
			return Integer.parseInt(str);
		} catch(NumberFormatException ex){
			return null;
		}
	}
	public static Integer parseInteger(String str, int default_value){
		Integer ret = parseInteger(str);
		return ret == null ? default_value : ret;
	}
	@SuppressLint("SimpleDateFormat")
	public static String getDateString(Date date){
		SimpleDateFormat format = new SimpleDateFormat();
		format.applyPattern(FORMAT_DATE);
		return format.format(date);
	}

	public static String getMimeType(String extension){
		// Fix content type
		if("log".equalsIgnoreCase(extension))
			extension = "txt";
		else if("patch".equalsIgnoreCase(extension))
			extension = "txt";

		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	}
}
