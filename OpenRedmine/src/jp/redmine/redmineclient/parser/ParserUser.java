package jp.redmine.redmineclient.parser;

import java.io.IOException;
import java.sql.SQLException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserUser extends BaseParser<RedmineConnection,RedmineUser> {
	//private final String TAG = this.toString();

	@Override
	public void parse(RedmineConnection con) throws XmlPullParserException, IOException, SQLException {
		super.parse(con);
		int eventType = xml.getEventType();
		RedmineUser item = null;
		Log.d("ParserUser","start parse");
		while (eventType != XmlPullParser.END_DOCUMENT) {
			eventType = xml.next();
			switch (eventType){
			case XmlPullParser.START_DOCUMENT:
				Log.d("ParserUser","START_DOCUMENT");
				break;
			case XmlPullParser.START_TAG:
				Log.d("ParserUser","START_TAG ".concat(xml.getName()));
				if("users".equalsIgnoreCase(xml.getName())){
					item = new RedmineUser();
				} else if(item != null){
					parseInternal(item);
				}
				break;
			case XmlPullParser.END_TAG:
				Log.d("ParserUser","END_TAG ".concat(xml.getName()));
				if("users".equalsIgnoreCase(xml.getName())){
					notifyDataCreation(con,item);
					item = null;
				}
				break;
			case XmlPullParser.TEXT:
				Log.d("ParserUser","TEXT ".concat(xml.getText()));
				break;
			}
		}

	}

	private void parseInternal(RedmineUser item) throws XmlPullParserException, IOException{
		if(xml.getDepth() <= 2)
			return;
		if("id".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			if("".equals(work))	return;
			item.setUserId(Integer.parseInt(work));
		} else if("login".equalsIgnoreCase(xml.getName())){
			item.setLoginName(getNextText());
		} else if("firstname".equalsIgnoreCase(xml.getName())){
			item.setFirstname(getNextText());
		} else if("lastname".equalsIgnoreCase(xml.getName())){
			item.setLastname(getNextText());
		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if("last_login_on".equalsIgnoreCase(xml.getName())){
			item.setModified(TypeConverter.parseDateTime(getNextText()));
		}

	}
}
