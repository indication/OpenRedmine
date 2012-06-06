package jp.redmine.redmineclient.external;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineStatus;

public class ParserStatus extends BaseParser<RedmineConnection,RedmineStatus> {
	//private final String TAG = this.toString();

	@Override
	public void parse(RedmineConnection con) throws XmlPullParserException, IOException {
		if (xml == null){
			Log.e("ParserStatus", "xml is null");
			return;
		}
		int eventType = xml.getEventType();
		RedmineStatus item = null;
		Log.d("ParserStatus","start parse");
		while (eventType != XmlPullParser.END_DOCUMENT) {
			eventType = xml.next();
			switch (eventType){
			case XmlPullParser.START_DOCUMENT:
				Log.d("ParserStatus","START_DOCUMENT");
				break;
			case XmlPullParser.START_TAG:
				Log.d("ParserStatus","START_TAG ".concat(xml.getName()));
				if("issue_category".equalsIgnoreCase(xml.getName())){
					item = new RedmineStatus();
				} else if(item != null){
					parseInternal(item);
				}
				break;
			case XmlPullParser.END_TAG:
				Log.d("ParserStatus","END_TAG ".concat(xml.getName()));
				if("issue_category".equalsIgnoreCase(xml.getName())){
					notifyDataCreation(con,item);
					item = null;
				}
				break;
			case XmlPullParser.TEXT:
				Log.d("ParserStatus","TEXT ".concat(xml.getText()));
				break;
			}
		}

	}

	private void parseInternal(RedmineStatus item) throws XmlPullParserException, IOException{
		if(xml.getDepth() <= 2)
			return;
		if("id".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			if("".equals(work))	return;
			item.setStatusId(Integer.parseInt(work));
		} else if("name".equalsIgnoreCase(xml.getName())){
			item.setName(getNextText());
		} else if("is_close".equalsIgnoreCase(xml.getName())){
			item.setIs_close("true".equalsIgnoreCase(getNextText()));
		} else if("is_default".equalsIgnoreCase(xml.getName())){
			item.setIs_default("true".equalsIgnoreCase(getNextText()));
		/*
		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.Created(TypeConverter.ParseDate(getNextText()));
		} else if("updated_on".equalsIgnoreCase(xml.getName())){
			item.Modified(TypeConverter.ParseDate(getNextText()));
		*/
		}

	}
}
