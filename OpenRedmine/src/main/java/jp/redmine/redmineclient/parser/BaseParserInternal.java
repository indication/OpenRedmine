package jp.redmine.redmineclient.parser;

import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;

import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.TypeConverter;

abstract public class BaseParserInternal<CON,ITEM> extends BaseParser<CON,ITEM> {
	private ITEM item = null;
	private int lastdepth;

	abstract protected String getProveTagName();
	abstract protected ITEM getNewProveTagItem();
	abstract protected void parseInternal(CON con,ITEM item)
			throws XmlPullParserException, IOException, SQLException;

	protected void onParseStart(CON con)
			throws XmlPullParserException, IOException, SQLException {
		item = null;
		lastdepth = 0;
	}
	@Override
	protected void onTagStart(CON con)
			throws XmlPullParserException, IOException, SQLException {
		if(equalsTagName(getProveTagName())){
			item = getNewProveTagItem();
			lastdepth = xml.getDepth();
		} else if(item != null){
			parseInternal(con,item);
		}
	}

	@Override
	protected void onTagEnd(CON con)
			throws XmlPullParserException, IOException,SQLException {
		if(equalsTagName(getProveTagName()) && lastdepth == xml.getDepth()){
			notifyDataCreation(con,item);
			item = null;
			lastdepth = 0;
		}
	}

	protected boolean equalsTagName(String tag)
			throws XmlPullParserException, IOException{
		return tag.equalsIgnoreCase(xml.getName());
	}

	protected void setMasterRecord(IMasterRecord item){
		item.setName(xml.getAttributeValue("", "name"));
		item.setRemoteId(getAttributeLong("id"));
	}

	protected Integer getAttributeInteger(String attr){
		return getAttributeInteger("",attr);
	}
	protected Integer getAttributeInteger(String schema, String attr){
		String id = xml.getAttributeValue(schema, attr);
		return TypeConverter.parseInteger(id);
	}


	protected Long getAttributeLong(String attr){
		return getAttributeLong("",attr);
	}
	protected Long getAttributeLong(String schema, String attr){
		String id = xml.getAttributeValue(schema, attr);
		return TextUtils.isEmpty(id) ? null : Long.parseLong(id);
	}

	protected String getAttributeString(String attr){
		return getAttributeString("",attr);
	}
	protected String getAttributeString(String schema, String attr){
		String id = xml.getAttributeValue(schema, attr);
		return TextUtils.isEmpty(id) ? null : id;
	}

	protected BigDecimal getAttributeBigDecimal(String attr){
		return getAttributeBigDecimal("",attr);
	}
	protected BigDecimal getAttributeBigDecimal(String schema, String attr){
		String id = xml.getAttributeValue(schema, attr);
		return TextUtils.isEmpty(id) ? null : new BigDecimal(id, MathContext.UNLIMITED);
	}
	
	protected Integer getTextInteger() throws XmlPullParserException, IOException{
		String work = getNextText();
		return TypeConverter.parseInteger(work);
	}
	
	protected Long getTextLong() throws XmlPullParserException, IOException{
		String work = getNextText();
		return TextUtils.isEmpty(work) ? null : Long.parseLong(work);
	}
	
	protected BigDecimal getTextBigDecimal() throws XmlPullParserException, IOException{
		String work = getNextText();
		return TextUtils.isEmpty(work) ? null : new BigDecimal(work, MathContext.UNLIMITED);
	}
}
