package jp.redmine.redmineclient.parser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserUser extends BaseParserInternal<RedmineConnection,RedmineUser> {

	@Override
	protected String getProveTagName() {
		return "user";//users
	}

	@Override
	protected RedmineUser getNewProveTagItem() {
		return new RedmineUser();
	}

	@Override
	protected void parseInternal(RedmineConnection con, RedmineUser item)
			throws XmlPullParserException, IOException, SQLException {
		if(xml.getDepth() <= 1)
			return;
		if("id".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			if("".equals(work))	return;
			item.setUserId(TypeConverter.parseInteger(work));
		} else if("login".equalsIgnoreCase(xml.getName())){
			item.setLoginName(getNextText());
		} else if("firstname".equalsIgnoreCase(xml.getName())){
			item.setFirstname(getNextText());
		} else if("lastname".equalsIgnoreCase(xml.getName())){
			item.setLastname(getNextText());
		} else if("mail".equalsIgnoreCase(xml.getName())){
			item.setMail(getNextText());
		} else if("api_key".equalsIgnoreCase(xml.getName())){
			//do nothing
		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if("last_login_on".equalsIgnoreCase(xml.getName())){
			item.setModified(TypeConverter.parseDateTime(getNextText()));
		}

	}
}
