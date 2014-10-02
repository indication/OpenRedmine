package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.util.Date;

@DatabaseTable
public class RedmineTimeEntry
		implements IConnectionRecord
		,IPostingRecord
		,IUserRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String TIMEENTRY_ID = "timeentry_id";
	public final static String PROJECT_ID = "project_id";
	public final static String ISSUE_ID = "issue_id";

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(uniqueIndexName="timeentry_target", columnName = RedmineConnection.CONNECTION_ID)
    private Integer connection_id;
    @DatabaseField(uniqueIndexName="timeentry_target")
    private Integer timeentry_id;
    @DatabaseField
    private Integer project_id;
    private RedmineProject project;
    @DatabaseField
    private Integer issue_id;
    private RedmineIssue issue;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "user_id", foreignAutoRefresh = true)
    private RedmineUser user;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "activity_id", foreignAutoRefresh = true)
    private RedmineTimeActivity activity;
    @DatabaseField
    private Date spents_on;
    @DatabaseField
    private BigDecimal hours;
    @DatabaseField
    private String comments;
    @DatabaseField
    private Date last_started;
    @DatabaseField
    private Date last_stopped;
    @DatabaseField
    private Date created;
    @DatabaseField
    private Date modified;
    @DatabaseField(defaultValue="false")
    private boolean is_dirty;


    /**
	 * @param id セットする id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return id
	 */
	public Long getId() {
		return id;
	}
	////////////////////////////////////////////////////////
	/**
	 * @return timeentry_id
	 */
	public Integer getTimeentryId() {
		return timeentry_id;
	}
	/**
	 * @param timeentry_id セットする timeentry_id
	 */
	public void setTimeentryId(Integer timeentry_id) {
		this.timeentry_id = timeentry_id;
	}
	/**
	 * @return project_id
	 */
	public Integer getProjectId() {
		return project_id;
	}
	/**
	 * @param project_id セットする project_id
	 */
	public void setProjectId(Integer project_id) {
		this.project_id = project_id;
	}
	/**
	 * @return project
	 */
	public RedmineProject getProject() {
		return project;
	}
	/**
	 * @param project セットする project
	 */
	public void setProject(RedmineProject project) {
		this.project = project;
	}
	/**
	 * @return issue_id
	 */
	public Integer getIssueId() {
		return issue_id;
	}
	/**
	 * @param issue_id セットする issue_id
	 */
	public void setIssueId(Integer issue_id) {
		this.issue_id = issue_id;
	}
	/**
	 * @return issue
	 */
	public RedmineIssue getIssue() {
		return issue;
	}
	/**
	 * @param issue セットする issue
	 */
	public void setIssue(RedmineIssue issue) {
		this.issue = issue;
	}
	/**
	 * @return user
	 */
	public RedmineUser getUser() {
		return user;
	}
	/**
	 * @param user セットする user
	 */
	public void setUser(RedmineUser user) {
		this.user = user;
	}
	/**
	 * @return activity
	 */
	public RedmineTimeActivity getActivity() {
		return activity;
	}
	/**
	 * @param activity セットする activity
	 */
	public void setActivity(RedmineTimeActivity activity) {
		this.activity = activity;
	}
	/**
	 * @return spents_on
	 */
	public Date getSpentsOn() {
		return spents_on;
	}
	/**
	 * @param spents_on セットする spents_on
	 */
	public void setSpentsOn(Date spents_on) {
		this.spents_on = spents_on;
	}
	/**
	 * @return hours
	 */
	public BigDecimal getHours() {
		return hours;
	}
	/**
	 * @param hours セットする hours
	 */
	public void setHours(BigDecimal hours) {
		this.hours = hours;
	}
	/**
	 * @return comments
	 */
	public String getComment() {
		return comments;
	}
	/**
	 * @param comments セットする comments
	 */
	public void setComment(String comments) {
		this.comments = comments;
	}
	@Override
	public void setRedmineConnection(RedmineConnection connection) {
		this.setConnectionId(connection.getId());
	}
	/**
	 * @param created セットする created
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	/**
	 * @return created
	 */
	public Date getCreated() {
		return created;
	}
	/**
	 * @param modified セットする modified
	 */
	public void setModified(Date modified) {
		this.modified = modified;
	}
	/**
	 * @return modified
	 */
	public Date getModified() {
		return modified;
	}


	/**
	 * @return is_dirty
	 */
	public boolean isDirty() {
		return is_dirty;
	}
	/**
	 * @param is_dirty セットする is_dirty
	 */
	public void setDirty(boolean is_dirty) {
		this.is_dirty = is_dirty;
	}

	@Override
	public void setConnectionId(Integer connection_id) {
		this.connection_id = connection_id;
	}
	@Override
	public Integer getConnectionId() {
		return connection_id;
	}

	@Override
	public Element getXml(Document document) {

		Element root = document.createElement("time_entry");
		if(this.getTimeentryId() != null){
			Element name = document.createElement("id");
			name.appendChild(document.createTextNode(String.valueOf(this.getTimeentryId())));
			root.appendChild(name);
		} else if(this.getIssueId() != null){
			Element name = document.createElement("issue_id");
			name.appendChild(document.createTextNode(String.valueOf(this.getIssueId())));
			root.appendChild(name);
		} else if(this.getProjectId() != null){
			Element name = document.createElement("project_id");
			name.appendChild(document.createTextNode(String.valueOf(this.getProjectId())));
			root.appendChild(name);
		}
		if(this.getSpentsOn() != null){
			Element name = document.createElement("spent_on");
			name.appendChild(document.createTextNode(String.format("%tF", this.getSpentsOn())));
			root.appendChild(name);
		}
		if(this.getHours() != null){
			Element name = document.createElement("hours");
			name.appendChild(document.createTextNode(this.getHours().toPlainString()));
			root.appendChild(name);
		}
		if(this.getActivity() != null && this.getActivity().getActivityId() != null){
			Element name = document.createElement("activity_id");
			name.appendChild(document.createTextNode(String.valueOf(this.getActivity().getActivityId())));
			root.appendChild(name);
		} else {
			throw new IllegalArgumentException("RedmineTimeEntry Activity is null or its id is null.");
		}
		if(this.getComment() != null){
			Element name = document.createElement("comments");
			name.appendChild(document.createTextNode(String.valueOf(this.getComment())));
			root.appendChild(name);
		}

		return root;
	}

}
