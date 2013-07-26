package jp.redmine.redmineclient.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

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
			throws XmlPullParserException, IOException{
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
		} else if("mail".equalsIgnoreCase(xml.getName())){
			item.setMail(getNextText());
		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if("last_login_on".equalsIgnoreCase(xml.getName())){
			item.setModified(TypeConverter.parseDateTime(getNextText()));
		}

	}
}
