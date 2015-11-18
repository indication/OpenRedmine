package jp.redmine.redmineclient.parser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserStatus extends BaseParserInternal<RedmineConnection,RedmineStatus> {

	@Override
	protected String getProveTagName() {
		return "issue_status";
	}

	@Override
	protected RedmineStatus getNewProveTagItem() {
		return new RedmineStatus();
	}

	@Override
	protected void parseInternal(RedmineConnection con, RedmineStatus item)
			throws XmlPullParserException, IOException, SQLException {
		if(xml.getDepth() <= 2)
			return;
		if("id".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			if("".equals(work))	return;
			item.setStatusId(TypeConverter.parseInteger(work));
		} else if("name".equalsIgnoreCase(xml.getName())){
			item.setName(getNextText());
		} else if("is_closed".equalsIgnoreCase(xml.getName())){
			item.setIs_close("true".equalsIgnoreCase(getNextText()));
		} else if("is_default".equalsIgnoreCase(xml.getName())){
			item.setIs_default("true".equalsIgnoreCase(getNextText()));

		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if("updated_on".equalsIgnoreCase(xml.getName())){
			item.setModified(TypeConverter.parseDateTime(getNextText()));
		}

	}
}
