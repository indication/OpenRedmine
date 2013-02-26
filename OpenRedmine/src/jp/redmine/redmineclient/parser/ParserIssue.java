package jp.redmine.redmineclient.parser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserIssue extends BaseParserInternal<RedmineConnection,RedmineIssue> {

	private ParserJournals parserJournal = new ParserJournals();
	@Override
	protected String getProveTagName() {
		return "issue";
	}

	@Override
	protected RedmineIssue getNewProveTagItem() {
		return new RedmineIssue();
	}

	@Override
	protected void parseInternal(RedmineConnection con, RedmineIssue item)
			throws XmlPullParserException, IOException{
		if(xml.getDepth() <= 1)
			return;
		if("id".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			if("".equals(work))	return;
			item.setIssueId(Integer.parseInt(work));
		} else if("subject".equalsIgnoreCase(xml.getName())){
			item.setSubject(getNextText());
		} else if("description".equalsIgnoreCase(xml.getName())){
			item.setDescription(getNextText());
		} else if("is_private".equalsIgnoreCase(xml.getName())){
			item.setPrivate("true".equalsIgnoreCase(getNextText()));


		} else if("project".equalsIgnoreCase(xml.getName())){
			RedmineProject pj = new RedmineProject();
			setMasterRecord(pj);
			item.setProject(pj);

		} else if("tracker".equalsIgnoreCase(xml.getName())){
			RedmineTracker tk = new RedmineTracker();
			setMasterRecord(tk);
			item.setTracker(tk);

		} else if("status".equalsIgnoreCase(xml.getName())){
			RedmineStatus tk = new RedmineStatus();
			setMasterRecord(tk);
			item.setStatus(tk);

		} else if("priority".equalsIgnoreCase(xml.getName())){
			RedminePriority tk = new RedminePriority();
			setMasterRecord(tk);
			item.setPriority(tk);

		} else if("category".equalsIgnoreCase(xml.getName())){
			RedmineProjectCategory tk = new RedmineProjectCategory();
			setMasterRecord(tk);
			item.setCategory(tk);

		} else if("assigned_to".equalsIgnoreCase(xml.getName())){
			RedmineUser tk = new RedmineUser();
			setMasterRecord(tk);
			item.setAssigned(tk);

		} else if("author".equalsIgnoreCase(xml.getName())){
			RedmineUser tk = new RedmineUser();
			setMasterRecord(tk);
			item.setAuthor(tk);

		} else if("fixed_version".equalsIgnoreCase(xml.getName())){
			RedmineProjectVersion tk = new RedmineProjectVersion();
			setMasterRecord(tk);
			item.setVersion(tk);

		} else if("start_date".equalsIgnoreCase(xml.getName())){
			item.setDateStart(TypeConverter.parseDate(getNextText()));
		} else if("due_date".equalsIgnoreCase(xml.getName())){
			item.setDateDue(TypeConverter.parseDate(getNextText()));

		} else if("done_ratio".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			short data = "".equals(work) ? 0 : Short.parseShort(work);
			item.setDoneRate(data);
			item.setProgressRate(data);

		} else if("estimated_hours".equalsIgnoreCase(xml.getName())){
			String work = getNextText();
			double data = "".equals(work) ? 0 : Double.parseDouble(work);
			item.setEstimatedHours(data);

		} else if(equalsTagName("journals")){
			final List<RedmineJournal> journals = new ArrayList<RedmineJournal>();
			parserJournal.setXml(xml);
			DataCreationHandler<RedmineIssue, RedmineJournal> handler =
				new DataCreationHandler<RedmineIssue, RedmineJournal>() {
				@Override
				public void onData(RedmineIssue info, RedmineJournal data)
						throws SQLException {
					journals.add(data);
				}
			};

			parserJournal.registerDataCreation(handler);
			try {
				parserJournal.parse(item);
			} catch (SQLException e) {
				Log.e("parserIssue","",e);
			}
			parserJournal.unregisterDataCreation(handler);
			item.setJournals(journals);

		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if("updated_on".equalsIgnoreCase(xml.getName())){
			item.setModified(TypeConverter.parseDateTime(getNextText()));
		}

	}


}
