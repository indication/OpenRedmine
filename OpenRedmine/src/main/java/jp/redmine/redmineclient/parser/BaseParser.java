package jp.redmine.redmineclient.parser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.BuildConfig;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
abstract public class BaseParser<CON,TYPE> {
	protected XmlPullParser xml;
	protected int dataCount = 0;
	private boolean isHalt;
	/**
	 * Stop the current parse in the <i>on*</i> method.
	 */
	protected void haltParse(){
		setHaltParse(true);
	}

	protected void setHaltParse(boolean isHalt){
		this.isHalt = isHalt;
	}

	public volatile List<DataCreationHandler<CON,TYPE>> handlerDataCreation = new ArrayList<DataCreationHandler<CON,TYPE>>();
	protected void onParseStart(CON con)
			throws XmlPullParserException, IOException, SQLException {
		if(BuildConfig.DEBUG_XML) Log.d("BaseParser","START");
	}
	protected void onParseEnd(CON con)
			throws XmlPullParserException, IOException, SQLException{
		if(BuildConfig.DEBUG_XML) Log.d("BaseParser","END");
	}
	protected void onDocumentStart(CON con)
			throws XmlPullParserException, IOException, SQLException {
		if(BuildConfig.DEBUG_XML) Log.d("BaseParser","START_DOCUMENT");
	}
	protected abstract void onTagStart(CON con)
			throws XmlPullParserException, IOException, SQLException;
	protected abstract void onTagEnd(CON con)
			throws XmlPullParserException, IOException, SQLException;
	protected void onText(CON con)
			throws XmlPullParserException, IOException, SQLException{
		if(BuildConfig.DEBUG_XML) Log.d("BaseParser","TEXT ".concat(xml.getText()));
	}

	public void setXml(XmlPullParser xml){
		if (xml == null){
			Log.e("ParserProject", "xml is null");
			return;
		}
		this.xml = xml;
	}

	public void parse(CON con) throws XmlPullParserException, IOException, SQLException{
		dataCount = 0;
		if (xml == null){
			Log.e("BaseParser", "xml is null");
			return;
		}
		setHaltParse(false);
		int eventType = xml.getEventType();
		onParseStart(con);
		while (eventType != XmlPullParser.END_DOCUMENT && !isHalt) {
			switch (eventType){
			case XmlPullParser.START_DOCUMENT:
				if(BuildConfig.DEBUG_XML) Log.d("BaseParser","END_DOCUMENT ");
				onDocumentStart(con);
				break;
			case XmlPullParser.START_TAG:
				if(BuildConfig.DEBUG_XML) Log.d("BaseParser","START_TAG ".concat(xml.getName()));
				onTagStart(con);
				break;
			case XmlPullParser.END_TAG:
				if(BuildConfig.DEBUG_XML) Log.d("BaseParser","END_TAG ".concat(xml.getName()));
				onTagEnd(con);
				break;
			case XmlPullParser.TEXT:
				if(BuildConfig.DEBUG_XML) Log.d("BaseParser","TEXT ".concat(xml.getText()));
				onText(con);
				break;
			}
			if(!isHalt)
				eventType = xml.next();
		}
		onParseEnd(con);
	}

	public int getCount(){
		return dataCount;
	}

	protected String getNextText() throws XmlPullParserException, IOException{
		String work = "";
		if(xml.next() == XmlPullParser.TEXT){
			work = xml.getText();
		}
		if(work == null)	work = "";
		if(BuildConfig.DEBUG_XML) Log.d("BaseParser","TEXT ".concat(work));
		return work;
	}

	public void registerDataCreation(DataCreationHandler<CON,TYPE> ev){
		this.handlerDataCreation.add(ev);
	}
	public void unregisterDataCreation(DataCreationHandler<CON,TYPE> ev){
		this.handlerDataCreation.remove(ev);
	}

	void notifyDataCreation(CON con, TYPE data) throws SQLException, IOException{
		dataCount++;
		//inherit from http://www.ibm.com/developerworks/jp/java/library/j-jtp07265/
		for(DataCreationHandler<CON,TYPE> ev:this.handlerDataCreation){
			try {
				ev.onData(con,data);
			} catch (RuntimeException e) {
				Log.e("notifyDataCreation","Catch exception",e);
				this.handlerDataCreation.remove(ev);
			}
		}
	}

}
