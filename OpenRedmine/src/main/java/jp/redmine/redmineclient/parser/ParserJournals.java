package jp.redmine.redmineclient.parser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedmineJournalChanges;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserJournals extends BaseParserInternal<RedmineIssue,RedmineJournal> {

	private ParserJournalChanges parserDetails = new ParserJournalChanges();
	@Override
	protected String getProveTagName() {
		return "journal";
	}

	@Override
	protected RedmineJournal getNewProveTagItem() {
		RedmineJournal journal =  new RedmineJournal();
		setJournalId(journal,xml.getAttributeValue("", "id"));
		return journal;
	}
	@Override
	protected void parseInternal(RedmineIssue con, RedmineJournal journal)
			throws XmlPullParserException, IOException, SQLException{


		if("id".equalsIgnoreCase(xml.getName())){
			setJournalId(journal,getNextText());
		} else if(equalsTagName("user")){
			RedmineUser user = new RedmineUser();
			setMasterRecord(user);
			journal.setUser(user);
		} else if(equalsTagName("notes")){
			journal.setNotes(getNextText());
		} else if(equalsTagName("created_on")){
			journal.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if(equalsTagName("details")){
			final List<RedmineJournalChanges> details = new ArrayList<RedmineJournalChanges>();
			parserDetails.setXml(xml);
			DataCreationHandler<RedmineJournal, RedmineJournalChanges> handler =
				new DataCreationHandler<RedmineJournal, RedmineJournalChanges>() {
				@Override
				public void onData(RedmineJournal info, RedmineJournalChanges data)
						throws SQLException {
					details.add(data);
				}
			};

			parserDetails.registerDataCreation(handler);
			parserDetails.parse(journal);
			parserDetails.unregisterDataCreation(handler);
			journal.setDetails(details);
		}


	}

	protected void setJournalId(RedmineJournal journal, String id){
		if("".equals(id))	return;
		journal.setJournalId(TypeConverter.parseInteger(id));

	}

	@Override
	protected void onTagEnd(RedmineIssue con)
		throws XmlPullParserException, IOException,SQLException {
		// stop parse appears end of the tag.
		if(equalsTagName("journals")){
			haltParse();
		} else {
			super.onTagEnd(con);
		}
	}
}
