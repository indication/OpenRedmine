package jp.redmine.redmineclient.external;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserIssue extends BaseParser<RedmineProject,RedmineIssue> {
	//private final String TAG = this.toString();

	@Override
	public void parse(RedmineProject con) throws XmlPullParserException, IOException {
		super.parse(con);
		int eventType = xml.getEventType();
		RedmineIssue item = null;
		Log.d("ParserIssue","start parse");
		while (eventType != XmlPullParser.END_DOCUMENT) {
			eventType = xml.next();
			switch (eventType){
			case XmlPullParser.START_DOCUMENT:
				Log.d("ParserIssue","START_DOCUMENT");
				break;
			case XmlPullParser.START_TAG:
				Log.d("ParserIssue","START_TAG ".concat(xml.getName()));
				if("issue".equalsIgnoreCase(xml.getName())){
					item = new RedmineIssue();
				} else if(item != null){
					parseInternal(item);
				}
				break;
			case XmlPullParser.END_TAG:
				Log.d("ParserIssue","END_TAG ".concat(xml.getName()));
				if("issue".equalsIgnoreCase(xml.getName())){
					notifyDataCreation(con,item);
					item = null;
				}
				break;
			case XmlPullParser.TEXT:
				Log.d("ParserIssue","TEXT ".concat(xml.getText()));
				break;
			}
		}

	}

	private void parseInternal(RedmineIssue item) throws XmlPullParserException, IOException{
		if(xml.getDepth() <= 2)
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
			item.setIsPrivate("true".equalsIgnoreCase(getNextText()));


		} else if("project".equalsIgnoreCase(xml.getName())){
			RedmineProject pj = new RedmineProject();
			pj.setName(xml.getAttributeValue("", "name"));
			String id = xml.getAttributeValue("", "id");
			if(!"".equals(id)){
				pj.setProjectId(Integer.parseInt(id));
			}
			item.setProject(pj);

		} else if("tracker".equalsIgnoreCase(xml.getName())){
			RedmineTracker tk = new RedmineTracker();
			tk.setName(xml.getAttributeValue("", "name"));
			String id = xml.getAttributeValue("", "id");
			if(!"".equals(id)){
				tk.setTrackerId(Integer.parseInt(id));
			}
			item.setTracker(tk);

		} else if("status".equalsIgnoreCase(xml.getName())){
			RedmineStatus tk = new RedmineStatus();
			tk.setName(xml.getAttributeValue("", "name"));
			String id = xml.getAttributeValue("", "id");
			if(!"".equals(id)){
				tk.setStatusId(Integer.parseInt(id));
			}
			item.setStatus(tk);

		} else if("priority".equalsIgnoreCase(xml.getName())){
			RedminePriority tk = new RedminePriority();
			tk.setName(xml.getAttributeValue("", "name"));
			String id = xml.getAttributeValue("", "id");
			if(!"".equals(id)){
				tk.setPriorityId(Integer.parseInt(id));
			}
			item.setPriority(tk);

		} else if("category".equalsIgnoreCase(xml.getName())){
			RedmineProjectCategory tk = new RedmineProjectCategory();
			tk.setName(xml.getAttributeValue("", "name"));
			String id = xml.getAttributeValue("", "id");
			if(!"".equals(id)){
				tk.setCategoryId(Integer.parseInt(id));
			}
			item.setCategory(tk);

		} else if("assigned_to".equalsIgnoreCase(xml.getName())){
			RedmineUser tk = new RedmineUser();
			tk.setFirstname(xml.getAttributeValue("", "name"));
			String id = xml.getAttributeValue("", "id");
			if(!"".equals(id)){
				tk.setUserId(Integer.parseInt(id));
			}
			item.setAssigned(tk);

		} else if("author".equalsIgnoreCase(xml.getName())){
			RedmineUser tk = new RedmineUser();
			tk.setFirstname(xml.getAttributeValue("", "name"));
			String id = xml.getAttributeValue("", "id");
			if(!"".equals(id)){
				tk.setUserId(Integer.parseInt(id));
			}
			item.setAuthor(tk);

		} else if("fixed_version".equalsIgnoreCase(xml.getName())){
			RedmineProjectVersion tk = new RedmineProjectVersion();
			tk.setName(xml.getAttributeValue("", "name"));
			String id = xml.getAttributeValue("", "id");
			if(!"".equals(id)){
				tk.setVersionId(Integer.parseInt(id));
			}
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

		} else if("created_on".equalsIgnoreCase(xml.getName())){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if("updated_on".equalsIgnoreCase(xml.getName())){
			item.setModified(TypeConverter.parseDateTime(getNextText()));
		}

	}
}
