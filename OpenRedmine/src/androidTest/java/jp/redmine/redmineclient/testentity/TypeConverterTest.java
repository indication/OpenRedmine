package jp.redmine.redmineclient.testentity;

import junit.framework.TestCase;

import java.util.Date;

import jp.redmine.redmineclient.entity.TypeConverter;

public class TypeConverterTest extends TestCase {
	public void testDateTimeUTC(){
		Date result = TypeConverter.parseDateTime("2014-04-30T13:51:56Z");
		assertNotNull(result);
	}
	public void testDateTimeLocal(){
		Date result = TypeConverter.parseDateTime("2014-04-30T13:51:56+0900");
		assertNotNull(result);
	}
	public void testDateTimeLocalMs(){
		Date result = TypeConverter.parseDateTime("2014-04-30T13:51:56.321+0900");
		assertNotNull(result);
	}

}
