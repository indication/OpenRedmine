package jp.redmine.redmineclient.parser;

import java.io.IOException;
import java.sql.SQLException;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserEnumerationIssuePriority extends BaseParserInternal<RedmineConnection,RedminePriority> {

	@Override
	protected String getProveTagName() {
		return "issue_priority";
	}

	@Override
	protected RedminePriority getNewProveTagItem() {
		return new RedminePriority();
	}

	@Override
	protected void parseInternal(RedmineConnection con, RedminePriority item)
			throws XmlPullParserException, IOException, SQLException {
		if(xml.getDepth() <= 2)
			return;
		if("id".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			if("".equals(work))	return;
			item.setRemoteId(Long.parseLong(work));
		} else if("name".equalsIgnoreCase(xml.getName())){
			item.setName(getNextText());
		} else if("is_default".equalsIgnoreCase(xml.getName())){
			item.setDefault("true".equalsIgnoreCase(getNextText()));

		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if("updated_on".equalsIgnoreCase(xml.getName())){
			item.setModified(TypeConverter.parseDateTime(getNextText()));
		}

	}
}
