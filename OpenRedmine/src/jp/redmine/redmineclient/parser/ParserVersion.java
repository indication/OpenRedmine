package jp.redmine.redmineclient.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserVersion extends BaseParserInternal<RedmineConnection,RedmineProjectVersion> {

	@Override
	protected String getProveTagName() {
		return "version";
	}

	@Override
	protected RedmineProjectVersion getNewProveTagItem() {
		return new RedmineProjectVersion();
	}

	@Override
	protected void parseInternal(RedmineConnection con, RedmineProjectVersion item)
			throws XmlPullParserException, IOException{
		if(xml.getDepth() <= 1)
			return;
		if("id".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			if("".equals(work))	return;
			item.setVersionId(Integer.parseInt(work));
		} else if("name".equalsIgnoreCase(xml.getName())){
			item.setName(getNextText());
		} else if("status".equalsIgnoreCase(xml.getName())){
			item.setStatus(getNextText());
		} else if("due_date".equalsIgnoreCase(xml.getName())){
			item.setDateDue(TypeConverter.parseDate(getNextText()));
		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if("updated_on".equalsIgnoreCase(xml.getName())){
			item.setModified(TypeConverter.parseDateTime(getNextText()));

		}

	}
}
