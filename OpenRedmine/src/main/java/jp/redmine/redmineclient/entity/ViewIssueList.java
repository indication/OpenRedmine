package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public class ViewIssueList
{
	public final static String ID = RedmineIssue.ID;
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String PROJECT_ID = RedmineIssue.PROJECT_ID;
	public final static String PROJECT_NAME = "project_name";
	public final static String ISSUE_ID = RedmineIssue.ISSUE_ID;
	public final static String DATE_START = RedmineIssue.DATE_START;
	public final static String DATE_DUE = RedmineIssue.DATE_DUE;
	public final static String DATE_CLOSED = RedmineIssue.DATE_CLOSED;
	public final static String MODIFIED = RedmineIssue.MODIFIED;
	public final static String CREATED = RedmineIssue.CREATED;
	public final static String SUBJECT = RedmineIssue.SUBJECT;
	public final static String PRIORITY = RedmineIssue.PRIORITY;
	public final static String PRIORITY_NAME = "priority_name";
	public final static String STATUS = RedmineIssue.STATUS;
	public final static String STATUS_NAME = "status_name";
	public final static String TRACKER = RedmineIssue.TRACKER;
	public final static String TRACKER_NAME = "tracker_name";
	public final static String VERSION = RedmineIssue.VERSION;
	public final static String VERSION_NAME = "version_name";
	public final static String CATEGORY = RedmineIssue.CATEGORY;
	public final static String ASSIGN = RedmineIssue.ASSIGN;
	public final static String ASSIGN_NAME = "assign_name";
	public final static String AUTHOR = RedmineIssue.AUTHOR;
	public final static String PROGRESS = RedmineIssue.PROGRESS;
	public final static String DONE_RATE = "done_rate";
	public final static String DESCRIPTION = RedmineIssue.DESCRIPTION;

	@DatabaseField(generatedId = true)
	public Long id;
	@DatabaseField
	public Integer connection_id;
	@DatabaseField
	public Long project_id;
	@DatabaseField
	public String project_name; //foregin
	@DatabaseField
	public Integer issue_id;
	@DatabaseField
	public Long tracker_id;
	@DatabaseField
	public String tracker_name; //foregin
	@DatabaseField
	public Long status_id;
	@DatabaseField
	public String status_name; //foregin
	@DatabaseField
	public Long priority_id;
	@DatabaseField
	public String priority_name; //foregin
	@DatabaseField
	public Long author_id;
	@DatabaseField
	public String author_name; //foregin
	@DatabaseField
	public Long assign_id;
	@DatabaseField
	public String assign_name; //foregin
	@DatabaseField
	public int category_id;
	@DatabaseField
	public String category_name; //foregin
	@DatabaseField
	public int version_id;
	@DatabaseField
	public String version_name; //foregin
	@DatabaseField
	public int parent_id;
	@DatabaseField
	public String subject;
	@DatabaseField
	public String description;
	@DatabaseField
	public Date start_date;
	@DatabaseField
	public Date due_date;
	@DatabaseField
	public Short progress_rate;
	@DatabaseField
	public Short done_rate;
	@DatabaseField
	public Double estimated_hours;
	@DatabaseField
	public boolean is_private;
	@DatabaseField
	public Date created;
	@DatabaseField
	public Date modified;
	@DatabaseField
	public Date data_modified;
	@DatabaseField
	public Date additional_modified;
	@DatabaseField
	public Date closed;

	public static void createTempViewStatement(StringBuilder sb) {
		sb.append("CREATE TEMP VIEW IF NOT EXISTS ");
		sb.append(ViewIssueList.class.getSimpleName());
		sb.append(" AS ");
		selectStatement(sb);
	}
	public static void selectStatement(StringBuilder sb){
		sb.append("SELECT ");
		sb.append(RedmineIssue.class.getSimpleName());
		sb.append(".*");
		sb.append(", project.name	AS project_name	");
		sb.append(", tracker.name	AS tracker_name	");
		sb.append(", status.name	AS status_name	");
		sb.append(", priority.name	AS priority_name");
		sb.append(", author.name	AS author_name	");
		sb.append(", assign.name	AS assign_name	");
		sb.append(", category.name	AS category_name");
		sb.append(", version.name	AS version_name	");
		sb.append(" FROM ");
			sb.append(RedmineIssue.class.getSimpleName());
			sb.append(" AS ");
			sb.append(RedmineIssue.class.getSimpleName());
		//project_name
		sb.append(" LEFT OUTER JOIN  ");
			sb.append(RedmineProject.class.getSimpleName());
			sb.append(" AS project ON ");
			sb.append(RedmineIssue.class.getSimpleName());	sb.append(".");	sb.append(PROJECT_ID);
			sb.append(" = project.");	sb.append(RedmineProject.ID);
		//tracker_name
		sb.append(" LEFT OUTER JOIN  ");
			sb.append(RedmineTracker.class.getSimpleName());
			sb.append(" AS tracker ON ");
			sb.append(RedmineIssue.class.getSimpleName());	sb.append(".");	sb.append(TRACKER);
			sb.append(" = tracker.");	sb.append(RedmineTracker.ID);
		//status_name
		sb.append(" LEFT OUTER JOIN  ");
			sb.append(RedmineStatus.class.getSimpleName());
			sb.append(" AS status ON ");
			sb.append(RedmineIssue.class.getSimpleName());	sb.append(".");	sb.append(STATUS);
			sb.append(" = status.");	sb.append(RedmineStatus.ID);
		//priority_name
		sb.append(" LEFT OUTER JOIN  ");
			sb.append(RedminePriority.class.getSimpleName());
			sb.append(" AS priority ON ");
			sb.append(RedmineIssue.class.getSimpleName());	sb.append(".");	sb.append(PRIORITY);
			sb.append(" = priority.");	sb.append(RedminePriority.ID);
		//author_name
		sb.append(" LEFT OUTER JOIN  ");
			sb.append(RedmineUser.class.getSimpleName());
			sb.append(" AS author ON ");
			sb.append(RedmineIssue.class.getSimpleName());	sb.append(".");	sb.append(AUTHOR);
			sb.append(" = author.");	sb.append(RedmineUser.ID);
		//assign_name
		sb.append(" LEFT OUTER JOIN  ");
			sb.append(RedmineUser.class.getSimpleName());
			sb.append(" AS assign ON ");
			sb.append(RedmineIssue.class.getSimpleName());	sb.append(".");	sb.append(ASSIGN);
			sb.append(" = assign.");	sb.append(RedmineUser.ID);
		//category_name
		sb.append(" LEFT OUTER JOIN  ");
			sb.append(RedmineProjectCategory.class.getSimpleName());
			sb.append(" AS category ON ");
			sb.append(RedmineIssue.class.getSimpleName());	sb.append(".");	sb.append(CATEGORY);
			sb.append(" = category.");	sb.append(RedmineProjectCategory.ID);
		//version_name
		sb.append(" LEFT OUTER JOIN  ");
			sb.append(RedmineProjectVersion.class.getSimpleName());
			sb.append(" AS version ON ");
			sb.append(RedmineIssue.class.getSimpleName());	sb.append(".");	sb.append(VERSION);
			sb.append(" = version.");	sb.append(RedmineProjectVersion.ID);
	}

}
