package jp.redmine.redmineclient.entity;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class TypeConverter {
	//Sat Sep 29 12:03:04 +0200 2007
	//E   M   d  H :m :s  Z     y
	public static final String FORMAT_DATETIME = "E M d H:m:s Z y";
	public static Date ParseDate(String datetime){
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_DATETIME);
		Date item = new Date();
		try {
			item = format.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return item;
	}
}
