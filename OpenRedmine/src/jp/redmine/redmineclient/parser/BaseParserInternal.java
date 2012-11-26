package jp.redmine.redmineclient.parser;

import java.io.IOException;
import java.sql.SQLException;

import org.xmlpull.v1.XmlPullParserException;

abstract public class BaseParserInternal<CON,ITEM> extends BaseParser<CON,ITEM> {
	private ITEM item = null;
	private int lastdepth;

	abstract protected String getProveTagName();
	abstract protected ITEM getNewProveTagItem();
	abstract protected void parseInternal(CON con,ITEM item)
			throws XmlPullParserException, IOException;

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
}
