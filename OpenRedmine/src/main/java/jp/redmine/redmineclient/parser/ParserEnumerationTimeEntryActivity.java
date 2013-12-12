package jp.redmine.redmineclient.parser;

import java.io.IOException;
import java.sql.SQLException;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTimeActivity;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserEnumerationTimeEntryActivity extends BaseParserInternal<RedmineConnection,RedmineTimeActivity> {

	@Override
	protected String getProveTagName() {
		return "time_entry_activity";
	}

	@Override
	protected RedmineTimeActivity getNewProveTagItem() {
		return new RedmineTimeActivity();
	}

	@Override
	protected void parseInternal(RedmineConnection con, RedmineTimeActivity item)
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
