package jp.redmine.redmineclient.parser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;

import jp.redmine.redmineclient.entity.RedmineNews;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserNews extends BaseParserInternal<RedmineProject,RedmineNews> {

	@Override
	protected String getProveTagName() {
		return "news";
	}

	@Override
	protected RedmineNews getNewProveTagItem() {
		return new RedmineNews();
	}

	@Override
	protected void parseInternal(RedmineProject con, RedmineNews item)
			throws XmlPullParserException, IOException, SQLException {
		if(xml.getDepth() <= 1)
			return;
		if(equalsTagName("id")){
			item.setNewsId(getTextInteger());
		} else if(equalsTagName("summary")){
			item.setSummary(getNextText());
		} else if(equalsTagName("title")){
			item.setTitle(getNextText());
		} else if(equalsTagName("description")){
			item.setDescription(getNextText());
		} else if(equalsTagName("author")){
			RedmineUser user = new RedmineUser();
			setMasterRecord(user);
			item.setUser(user);
		} else if("project".equalsIgnoreCase(xml.getName())){
			RedmineProject pj = new RedmineProject();
			setMasterRecord(pj);
			item.setProject(pj);
		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if("updated_on".equalsIgnoreCase(xml.getName())){
			item.setModified(TypeConverter.parseDateTime(getNextText()));
		}

	}
}
