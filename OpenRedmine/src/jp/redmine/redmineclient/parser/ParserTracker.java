package jp.redmine.redmineclient.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTracker;

public class ParserTracker extends BaseParser<RedmineConnection,RedmineTracker> {
	//private final String TAG = this.toString();

	@Override
	public void parse(RedmineConnection con) throws XmlPullParserException, IOException {
		super.parse(con);
		int eventType = xml.getEventType();
		RedmineTracker item = null;
		Log.d("ParserTracker","start parse");
		while (eventType != XmlPullParser.END_DOCUMENT) {
			eventType = xml.next();
			switch (eventType){
			case XmlPullParser.START_DOCUMENT:
				Log.d("ParserTracker","START_DOCUMENT");
				break;
			case XmlPullParser.START_TAG:
				Log.d("ParserTracker","START_TAG ".concat(xml.getName()));
				if("tracker".equalsIgnoreCase(xml.getName())){
					item = new RedmineTracker();
				} else if(item != null){
					parseInternal(item);
				}
				break;
			case XmlPullParser.END_TAG:
				Log.d("ParserTracker","END_TAG ".concat(xml.getName()));
				if("tracker".equalsIgnoreCase(xml.getName())){
					notifyDataCreation(con,item);
					item = null;
				}
				break;
			case XmlPullParser.TEXT:
				Log.d("ParserTracker","TEXT ".concat(xml.getText()));
				break;
			}
		}

	}

	private void parseInternal(RedmineTracker item) throws XmlPullParserException, IOException{
		if(xml.getDepth() <= 2)
			return;
		if("id".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			if("".equals(work))	return;
			item.setTrackerId(Integer.parseInt(work));
		} else if("name".equalsIgnoreCase(xml.getName())){
			item.setName(getNextText());
		/*
		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.Created(TypeConverter.ParseDate(getNextText()));
		} else if("updated_on".equalsIgnoreCase(xml.getName())){
			item.Modified(TypeConverter.ParseDate(getNextText()));
		*/
		}

	}
}
