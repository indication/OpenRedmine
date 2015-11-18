package jp.redmine.redmineclient.parser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineTimeActivity;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserTimeEntry extends BaseParserInternal<RedmineConnection,RedmineTimeEntry> {

	@Override
	protected String getProveTagName() {
		return "time_entry";
	}

	@Override
	protected RedmineTimeEntry getNewProveTagItem() {
		return new RedmineTimeEntry();
	}

	@Override
	protected void parseInternal(RedmineConnection con, RedmineTimeEntry item)
			throws XmlPullParserException, IOException, SQLException {
		if(xml.getDepth() <= 1)
			return;
		if(equalsTagName("id")){
			item.setTimeentryId(TypeConverter.parseInteger(getNextText()));
		} else if(equalsTagName("project")){
			RedmineProject entity = new RedmineProject();
			setMasterRecord(entity);
			item.setProject(entity);
			item.setProjectId(getAttributeInteger("id"));
		} else if(equalsTagName("issue")){
			item.setIssueId(getAttributeInteger("id"));
		} else if(equalsTagName("user")){
			RedmineUser entity = new RedmineUser();
			setMasterRecord(entity);
			item.setUser(entity);
		} else if(equalsTagName("activity")){
			RedmineTimeActivity entity = new RedmineTimeActivity();
			setMasterRecord(entity);
			item.setActivity(entity);
		} else if(equalsTagName("comments")){
			item.setComment(getNextText());
		} else if(equalsTagName("hours")){
			item.setHours(TypeConverter.parseBigDecimal(getNextText()));
		} else if(equalsTagName("spent_on")){
			item.setSpentsOn(TypeConverter.parseDate(getNextText()));
		} else if(equalsTagName("created_on")){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if(equalsTagName("updated_on")){
			item.setModified(TypeConverter.parseDateTime(getNextText()));
		}

	}
}
