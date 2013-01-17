package jp.redmine.redmineclient.parser;

import java.io.IOException;
import java.sql.SQLException;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserJournals extends BaseParserInternal<RedmineIssue,RedmineJournal> {

	@Override
	protected String getProveTagName() {
		return "journal";
	}

	@Override
	protected RedmineJournal getNewProveTagItem() {
		return new RedmineJournal();
	}
	@Override
	protected void parseInternal(RedmineIssue con, RedmineJournal journal)
			throws XmlPullParserException, IOException{


		if("id".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			if("".equals(work))	return;
			journal.setJournnalId(Integer.parseInt(work));
		} else if(equalsTagName("user")){
			RedmineUser user = new RedmineUser();
			setMasterRecord(user);
			journal.setUser(user);
		} else if(equalsTagName("user")){
			journal.setNotes(getNextText());
		} else if(equalsTagName("created_on")){
			journal.setCreated(TypeConverter.parseDateTime(getNextText()));
		}


	}

	protected void setMasterRecord(IMasterRecord item)
		throws XmlPullParserException, IOException{
		item.setName(xml.getAttributeValue("", "name"));
		String id = xml.getAttributeValue("", "id");
		if(!"".equals(id)){
			item.setRemoteId(Long.parseLong(id));
		}
	}

	@Override
	protected void onTagEnd(RedmineIssue con)
		throws XmlPullParserException, IOException,SQLException {
		// stop parse appears end of the tag.
		if(equalsTagName("journals")){
			haltParse();
		}
	}
}
