package jp.redmine.redmineclient.entity;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DatabaseTable
public class RedmineIssue
		implements IConnectionRecord
		,IPostingRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String PROJECT_ID = "project_id";
	public final static String ISSUE_ID = "issue_id";
	public final static String DATE_START = "start_date";
	public final static String DATE_DUE = "due_date";
	public final static String DATE_CLOSED = "closed";
	public final static String MODIFIED = "modified";
	public final static String CREATED = "created";
	public final static String SUBJECT = "subject";
	public final static String PRIORITY = "priority_id";
	public final static String STATUS = "status_id";
	public final static String TRACKER = "tracker_id";
	public final static String VERSION = "version_id";
	public final static String CATEGORY = "category_id";
	public final static String ASSIGN = "assign_id";
	public final static String AUTHOR = "author_id";
	public final static String PROGRESS = "progress_rate";
	public final static String DESCRIPTION = "description";

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(uniqueIndexName="issue_target", columnName = RedmineConnection.CONNECTION_ID)
    private Integer connection_id;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "project_id", foreignAutoRefresh = true)
    private RedmineProject project;
    @DatabaseField(uniqueIndexName="issue_target")
    private Integer issue_id;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "tracker_id", foreignAutoRefresh = true)
    private RedmineTracker tracker;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "status_id", foreignAutoRefresh = true)
    private RedmineStatus status;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "priority_id", foreignAutoRefresh = true)
    private RedminePriority priority;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "author_id", foreignAutoRefresh = true)
    private RedmineUser author;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "assign_id", foreignAutoRefresh = true)
    private RedmineUser assigned;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "category_id", foreignAutoRefresh = true)
    private RedmineProjectCategory category;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "version_id", foreignAutoRefresh = true)
    private RedmineProjectVersion version;
    @DatabaseField
    private int parent_id;
    @DatabaseField
    private String subject;
    @DatabaseField
    private String description;
    @DatabaseField
    private Date start_date;
    @DatabaseField
    private Date due_date;
    @DatabaseField
    private Short progress_rate;
    @DatabaseField
    private Short done_rate;
    @DatabaseField
    private Double estimated_hours;
    @DatabaseField
    private boolean is_private;
    @DatabaseField
    private Date created;
    @DatabaseField
    private Date modified;
    @DatabaseField
    private Date data_modified;
    @DatabaseField
    private Date additional_modified;
    @DatabaseField
    private Date closed;

    private BigDecimal done_hours;

	private List<RedmineJournal> journals;
	private List<RedmineIssueRelation> relations;
	private List<RedmineAttachment> attachments;
	private List<RedmineWatcher> watchers;

	static public void setupConnectionId(RedmineIssue item){
		if(item.getConnectionId() == null)
			return;
		if(item.getTracker() != null)
			item.getTracker().setConnectionId(item.getConnectionId());
		if(item.getAuthor() != null)
			item.getAuthor().setConnectionId(item.getConnectionId());
		if(item.getCategory() != null)
			item.getCategory().setConnectionId(item.getConnectionId());
		if(item.getAssigned() != null)
			item.getAssigned().setConnectionId(item.getConnectionId());
		if(item.getStatus() != null)
			item.getStatus().setConnectionId(item.getConnectionId());
		if(item.getProject() != null)
			item.getProject().setConnectionId(item.getConnectionId());
		if(item.getVersion() != null)
			item.getVersion().setConnectionId(item.getConnectionId());
		if(item.getPriority() != null)
			item.getPriority().setConnectionId(item.getConnectionId());
	}

	static public void setupProjectId(RedmineIssue item){
		if(item.getVersion() != null)
			item.getVersion().setProject(item.getProject());
		if(item.getCategory() != null)
			item.getCategory().setProject(item.getProject());

	}
	static public void setupJournals(RedmineIssue item){
		if(item.getJournals() == null)
			return;
		for (RedmineJournal data : item.getJournals()){
			data.setConnectionId(item.getConnectionId());
			data.setIssueId(item.getId());
			if(data.getUser() != null){
				data.getUser().setConnectionId(item.getConnectionId());
			}
		}
	}
	static public void setupRelations(RedmineIssue item){
		if(item.getRelations() == null)
			return;
		for (RedmineIssueRelation data : item.getRelations()){
			data.setConnectionId(item.getConnectionId());
		}
	}
	static public void setupAttachments(RedmineIssue item){
		if(item.getAttachments() == null)
			return;
		for (RedmineAttachment data : item.getAttachments()){
			data.setConnectionId(item.getConnectionId());
			data.setIssueId(item.getIssueId());
		}
	}
	static public void setupWatchers(RedmineIssue item){
		if(item.getWatchers() == null)
			return;
		for (RedmineWatcher data : item.getWatchers()){
			data.setConnectionId(item.getConnectionId());
			data.setIssueId(item.getIssueId());
			if(data.getUser() != null)
				data.getUser().setConnectionId(item.getConnectionId());
		}
	}

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
	 * @param description セットする description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
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
	 * @param project セットする project
	 */
	public void setProject(RedmineProject project) {
		this.project = project;
	}


	/**
	 * @return project
	 */
	public RedmineProject getProject() {
		return project;
	}

	@Override
	public void setConnectionId(Integer connection_id) {
		this.connection_id = connection_id;
	}
	@Override
	public Integer getConnectionId() {
		return connection_id;
	}


	/**
	 * @param issue_id セットする issue_id
	 */
	public void setIssueId(Integer issue_id) {
		this.issue_id = issue_id;
	}


	/**
	 * @return issue_id
	 */
	public Integer getIssueId() {
		return issue_id;
	}


	/**
	 * @param tracker セットする tracker
	 */
	public void setTracker(RedmineTracker tracker) {
		this.tracker = tracker;
	}


	/**
	 * @return tracker
	 */
	public RedmineTracker getTracker() {
		return tracker;
	}


	/**
	 * @param status セットする status
	 */
	public void setStatus(RedmineStatus status) {
		this.status = status;
	}


	/**
	 * @return status
	 */
	public RedmineStatus getStatus() {
		return status;
	}


	/**
	 * @param priority セットする priority
	 */
	public void setPriority(RedminePriority priority) {
		this.priority = priority;
	}


	/**
	 * @return priority
	 */
	public RedminePriority getPriority() {
		return priority;
	}


	/**
	 * @param author セットする author
	 */
	public void setAuthor(RedmineUser author) {
		this.author = author;
	}


	/**
	 * @return author
	 */
	public RedmineUser getAuthor() {
		return author;
	}


	/**
	 * @param assigned セットする assigned
	 */
	public void setAssigned(RedmineUser assigned) {
		this.assigned = assigned;
	}


	/**
	 * @return assigned
	 */
	public RedmineUser getAssigned() {
		return assigned;
	}


	/**
	 * @param category セットする category
	 */
	public void setCategory(RedmineProjectCategory category) {
		this.category = category;
	}


	/**
	 * @return category
	 */
	public RedmineProjectCategory getCategory() {
		return category;
	}


	/**
	 * @param subject セットする subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}


	/**
	 * @return subject
	 */
	public String getSubject() {
		return subject;
	}


	/**
	 * @param start_date セットする start_date
	 */
	public void setDateStart(Date start_date) {
		this.start_date = start_date;
	}


	/**
	 * @return start_date
	 */
	public Date getDateStart() {
		return start_date;
	}


	/**
	 * @param due_date セットする due_date
	 */
	public void setDateDue(Date due_date) {
		this.due_date = due_date;
	}


	/**
	 * @return due_date
	 */
	public Date getDateDue() {
		return due_date;
	}


	/**
	 * @param progress_rate セットする progress_rate
	 */
	public void setProgressRate(Short progress_rate) {
		this.progress_rate = progress_rate;
	}


	/**
	 * @return progress_rate
	 */
	public Short getProgressRate() {
		return progress_rate;
	}


	/**
	 * @param done_rate セットする done_rate
	 */
	public void setDoneRate(Short done_rate) {
		this.done_rate = done_rate;
	}


	/**
	 * @return done_rate
	 */
	public Short getDoneRate() {
		return done_rate;
	}


	/**
	 * @param estimated_hours セットする estimated_hours
	 */
	public void setEstimatedHours(Double estimated_hours) {
		this.estimated_hours = estimated_hours;
	}


	/**
	 * @return estimated_hours
	 */
	public Double getEstimatedHours() {
		return estimated_hours;
	}


	public BigDecimal getDoneHours() {
		return done_hours;
	}

	public void setDoneHours(BigDecimal done_hours) {
		this.done_hours = done_hours;
	}

	/**
	 * @param is_private セットする is_private
	 */
	public void setPrivate(boolean is_private) {
		this.is_private = is_private;
	}


	/**
	 * @return is_private
	 */
	public boolean isPrivate() {
		return is_private;
	}


	@Override
	public void setRedmineConnection(RedmineConnection info) {
		setConnectionId(info.getId());
	}


	/**
	 * @param version セットする version
	 */
	public void setVersion(RedmineProjectVersion version) {
		this.version = version;
	}


	/**
	 * @return version
	 */
	public RedmineProjectVersion getVersion() {
		return version;
	}


	/**
	 * @param parent_id セットする parent_id
	 */
	public void setParentId(int parent_id) {
		this.parent_id = parent_id;
	}


	/**
	 * @return parent_id
	 */
	public int getParentId() {
		return parent_id;
	}

	/**
	 * @return journals
	 */
	public List<RedmineJournal> getJournals() {
		return journals;
	}

	/**
	 * @param journals セットする journals
	 */
	public void setJournals(List<RedmineJournal> journals) {
		this.journals = journals;
	}

	public List<RedmineIssueRelation> getRelations() {
		return relations;
	}

	public void setRelations(List<RedmineIssueRelation> relations) {
		this.relations = relations;
	}

	public List<RedmineAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<RedmineAttachment> attachments) {
		this.attachments = attachments;
	}

	/**
	 * Get first item of journals
	 * @return journals
	 */
	public RedmineJournal getJournal() {
		if(journals == null)
			return null;
		if(journals.size() < 1)
			return null;
		return journals.get(0);
	}

	/**
	 * Set journal for posting note
	 * @param journal for notes
	 */
	public void setJournal(RedmineJournal journal) {
		this.journals = new ArrayList<RedmineJournal>();
		this.journals.add(journal);
	}

	/**
	 * @param data_modified セットする data_modified
	 */
	public void setDataModified(Date data_modified) {
		this.data_modified = data_modified;
	}

	/**
	 * @return data_modified
	 */
	public Date getDataModified() {
		return data_modified;
	}

	/**
	 * @param additional_modified セットする additional_modified
	 */
	public void setAdditionalModified(Date additional_modified) {
		this.additional_modified = additional_modified;
	}

	/**
	 * @return additional_modified
	 */
	public Date getAdditionalModified() {
		return additional_modified;
	}

	public void setClosed(Date date) {
		closed = date;
	}

	/**
	 * @return closed
	 */
	public Date getClosed() {
		return closed;
	}

	public List<RedmineWatcher> getWatchers() {
		return watchers;
	}

	public void setWatchers(List<RedmineWatcher> watchers) {
		this.watchers = watchers;
	}

	@Override
	public Element getXml(Document document) {
		Element root = document.createElement("issue");
		if(getIssueId() != null){
			root.appendChild(getElement(document,"id",		String.valueOf(this.getIssueId())));
		}
		root.appendChild(getElement(document,"project_id",	getProject()));
		root.appendChild(getElement(document,"tracker_id",	getTracker()));
		root.appendChild(getElement(document,"status_id",	getStatus()));
		if(!TextUtils.isEmpty(getSubject())){
			root.appendChild(getElement(document,"subject",		this.getSubject()));
		} else {
			throw new IllegalArgumentException("RedmineIssue Subject is empty.");
		}
		if(!TextUtils.isEmpty(getDescription())){
			root.appendChild(getElement(document,"description",	this.getDescription()));
		}
		root.appendChild(getElement(document,"category_id",		getCategory()));
		root.appendChild(getElement(document,"assigned_to_id",	getAssigned()));
		if(getParentId() != 0){
			root.appendChild(getElement(document,"parent_issue_id",String.valueOf(this.getParentId())));
		}
		root.appendChild(getElement(document,"fixed_version_id",	getVersion()));
		root.appendChild(getElement(document,"priority_id",		getPriority()));
		root.appendChild(getElement(document,"done_ratio",		String.valueOf(this.getDoneRate())));
		root.appendChild(getElement(document,"start_date",		getDateStart()));
		root.appendChild(getElement(document,"due_date",		getDateDue()));
		root.appendChild(getElement(document,"estimated_hours",	getEstimatedHours() == null || getEstimatedHours() == 0 ? "" : String.valueOf(this.getEstimatedHours())));
		RedmineJournal journal = getJournal();
		if( journal != null && !TextUtils.isEmpty(journal.getNotes()) ){
			root.appendChild(getElement(document,"notes",		journal.getNotes()));
		}
		return root;
	}

	protected Element getElement(Document document, String name, IMasterRecord record){
		return getElement(document, name, record == null ? "" : String.valueOf(record.getRemoteId()));
	}
	protected Element getElement(Document document, String name, Date record){
		return getElement(document, name, record == null ? "" : TypeConverter.getDateString(record));
	}
	protected Element getElement(Document document, String name, String record){
		Element element = document.createElement(name);
		element.appendChild(document.createTextNode(record == null ? "" : record));
		return element;
	}


}
