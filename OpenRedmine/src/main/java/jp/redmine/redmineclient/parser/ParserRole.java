package jp.redmine.redmineclient.parser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineRole;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserRole extends BaseParserInternal<RedmineConnection, RedmineRole> {

	@Override
	protected String getProveTagName() {
		return "role";
	}

	@Override
	protected RedmineRole getNewProveTagItem() {
		return new RedmineRole();
	}

	@Override
	protected void parseInternal(RedmineConnection con, RedmineRole item)
			throws XmlPullParserException, IOException, SQLException {
		if (xml.getDepth() <= 2)
			return;
		if (equalsTagName("id")) {
			String work = getNextText();
			if ("".equals(work)) return;
			item.setRoleId(TypeConverter.parseInteger(work));
		} else if (equalsTagName("name")) {
			item.setName(getNextText());
		} else if (equalsTagName("permissions ")) {
			//todo implement to set permissions
		} else if (equalsTagName("created_on")) {
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if (equalsTagName("updated_on")) {
			item.setModified(TypeConverter.parseDateTime(getNextText()));
		}

	}
}
