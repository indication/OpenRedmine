package jp.redmine.redmineclient.parser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserCategory extends BaseParserInternal<RedmineConnection,RedmineProjectCategory> {

	@Override
	protected String getProveTagName() {
		return "issue_category";
	}

	@Override
	protected RedmineProjectCategory getNewProveTagItem() {
		return new RedmineProjectCategory();
	}

	@Override
	protected void parseInternal(RedmineConnection con, RedmineProjectCategory item)
			throws XmlPullParserException, IOException, SQLException {
		if(xml.getDepth() <= 1)
			return;
		if("id".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			if("".equals(work))	return;
			item.setCategoryId(TypeConverter.parseInteger(work));
		} else if("name".equalsIgnoreCase(xml.getName())){
			item.setName(getNextText());
		} else if("assigned_to".equalsIgnoreCase(xml.getName())){
			RedmineUser user = new RedmineUser();
			String data = xml.getAttributeValue("", "id");
			if(data != null){
				user.setUserId(TypeConverter.parseInteger(data));
			}
			user.setConnectionId(con.getId());
			user.setName(xml.getAttributeValue("", "name"));
			item.setAssignTo(user);
		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if("updated_on".equalsIgnoreCase(xml.getName())){
			item.setModified(TypeConverter.parseDateTime(getNextText()));

		}

	}
}
