package jp.redmine.redmineclient.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineStatus;

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
			throws XmlPullParserException, IOException{
		if(xml.getDepth() <= 2)
			return;
		if("id".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			if("".equals(work))	return;
			item.setStatusId(Integer.parseInt(work));
		} else if("name".equalsIgnoreCase(xml.getName())){
			item.setName(getNextText());
		} else if("is_close".equalsIgnoreCase(xml.getName())){
			item.setIs_close("true".equalsIgnoreCase(getNextText()));
		} else if("is_default".equalsIgnoreCase(xml.getName())){
			item.setIs_default("true".equalsIgnoreCase(getNextText()));
		/*
		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.Created(TypeConverter.ParseDate(getNextText()));
		} else if("updated_on".equalsIgnoreCase(xml.getName())){
			item.Modified(TypeConverter.ParseDate(getNextText()));
		*/
		}

	}
}
