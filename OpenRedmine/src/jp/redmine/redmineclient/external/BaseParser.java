package jp.redmine.redmineclient.external;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
abstract public class BaseParser<CON,TYPE> {
	protected XmlPullParser xml;
	protected int dataCount = 0;
	public volatile List<DataCreationHandler<CON,TYPE>> handlerDataCreation = new ArrayList<DataCreationHandler<CON,TYPE>>();
	public void setXml(XmlPullParser xml){
		if (xml == null){
			Log.e("ParserProject", "xml is null");
			return;
		}
		this.xml = xml;
	}

	public void parse(CON con) throws XmlPullParserException, IOException{
		dataCount = 0;
		if (xml == null){
			Log.e("BaseParser", "xml is null");
			return;
		}
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
		return work;
	}

	public void registerDataCreation(DataCreationHandler<CON,TYPE> ev){
		this.handlerDataCreation.add(ev);
	}
	public void unregisterDataCreation(DataCreationHandler<CON,TYPE> ev){
		this.handlerDataCreation.remove(ev);
	}

	protected void notifyDataCreation(CON con,TYPE data){
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
