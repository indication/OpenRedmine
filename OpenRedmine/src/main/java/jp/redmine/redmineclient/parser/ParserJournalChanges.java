package jp.redmine.redmineclient.parser;

import java.io.IOException;
import java.sql.SQLException;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedmineJournalChanges;

public class ParserJournalChanges extends BaseParserInternal<RedmineJournal,RedmineJournalChanges> {

	@Override
	protected String getProveTagName() {
		return "detail";
	}

	@Override
	protected RedmineJournalChanges getNewProveTagItem() {
		RedmineJournalChanges changes = new RedmineJournalChanges();
		changes.setName(xml.getAttributeValue("", "name"));
		changes.setProperty(xml.getAttributeValue("", "property"));
		return changes;
	}
	@Override
	protected void parseInternal(RedmineJournal con, RedmineJournalChanges journal)
			throws XmlPullParserException, IOException, SQLException{


		if(equalsTagName("old_value")){
			journal.setBefore(getNextText());
		} else if(equalsTagName("new_value")){
			journal.setAfter(getNextText());
		}


	}

	@Override
	protected void onTagEnd(RedmineJournal con)
		throws XmlPullParserException, IOException,SQLException {
		// stop parse appears end of the tag.
		if(equalsTagName("details")){
			haltParse();
		} else {
			super.onTagEnd(con);
		}
	}
}
