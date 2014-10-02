package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public class RedmineRecentIssue
		implements IConnectionRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String PROJECT = "project_id";
	public final static String ISSUE = "issue_id";
	public final static String MODIFIED = "modified";

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField(uniqueIndexName="history_target", columnName = RedmineConnection.CONNECTION_ID)
	private Integer connection_id;
	@DatabaseField(foreign = true,foreignColumnName="id", columnName= PROJECT, foreignAutoRefresh = true)
	private RedmineProject project;
    @DatabaseField(uniqueIndexName="history_target", foreign = true,foreignColumnName="id", columnName= ISSUE, foreignAutoRefresh = true)
	private RedmineIssue issue;
	@DatabaseField
	private int count;
	@DatabaseField
	private Date created;
	@DatabaseField
	private Date modified;

	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}

	public RedmineProject getProject() {
		return project;
	}
	public void setProject(RedmineProject project) {
		this.project = project;
	}

	@Override
	public void setRedmineConnection(RedmineConnection connection) {
		this.setConnectionId(connection.getId());
	}
	@Override
	public void setConnectionId(Integer connection_id) {
		this.connection_id = connection_id;
	}
	@Override
	public Integer getConnectionId() {
		return connection_id;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getCreated() {
		return created;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
	public Date getModified() {
		return modified;
	}

	public RedmineIssue getIssue() {
		return issue;
	}
	public void setIssue(RedmineIssue issue) {
		this.issue = issue;
	}

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void countup(){
		count++;
	}

}
