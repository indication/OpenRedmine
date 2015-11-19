package jp.redmine.redmineclient.parser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserTracker extends BaseParserInternal<RedmineConnection,RedmineTracker> {

	@Override
	protected String getProveTagName() {
		return "tracker";
	}

	@Override
	protected RedmineTracker getNewProveTagItem() {
		return new RedmineTracker();
	}

	@Override
	protected void parseInternal(RedmineConnection con, RedmineTracker item)
			throws XmlPullParserException, IOException, SQLException {
		if(xml.getDepth() <= 2)
			return;
		if("id".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			if("".equals(work))	return;
			item.setTrackerId(TypeConverter.parseInteger(work));
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
