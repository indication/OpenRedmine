package jp.redmine.redmineclient.parser;

import java.io.IOException;
import java.sql.SQLException;

import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineIssueRelation;
import jp.redmine.redmineclient.entity.RedmineIssueRelation.RelationType;
import jp.redmine.redmineclient.entity.TypeConverter;

import org.xmlpull.v1.XmlPullParserException;

public class ParserIssueRelations extends BaseParserInternal<RedmineIssue,RedmineIssueRelation> {

	@Override
	protected String getProveTagName() {
		return "relation";
	}

	@Override
	protected RedmineIssueRelation getNewProveTagItem() {
		RedmineIssueRelation relation =  new RedmineIssueRelation();
		return relation;
	}
	@Override
	protected void parseInternal(RedmineIssue con, RedmineIssueRelation item)
			throws XmlPullParserException, IOException, SQLException{

		if(equalsTagName("id")){
			item.setRelationId(getTextInteger());
		} else if(equalsTagName("issue_id")){
			item.setIssueId(getTextInteger());
		} else if(equalsTagName("issue_to_id")){
			item.setIssueToId(getTextInteger());
		} else if(equalsTagName("delay")){
			item.setDelay(getTextBigDecimal());
		} else if(equalsTagName("relation_type")){
			item.setType(RelationType.getValueOf(getNextText()));
		} else if(equalsTagName("created_on")){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if(equalsTagName("updated_on")){
			item.setModified(TypeConverter.parseDateTime(getNextText()));
		}


	}

	@Override
	protected void onTagEnd(RedmineIssue con)
		throws XmlPullParserException, IOException,SQLException {
		// stop parse appears end of the tag.
		if(equalsTagName("relations")){
			haltParse();
		} else {
			super.onTagEnd(con);
		}
	}
}
