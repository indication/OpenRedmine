package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public class RedmineWatcher
		implements IConnectionRecord
		,IUserRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String ISSUE_ID = "issue_id";
	public final static String USER_ID = "user_id";

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField(uniqueIndexName="watcher_target",columnName = RedmineConnection.CONNECTION_ID)
	private Integer connection_id;
	@DatabaseField(uniqueIndexName="watcher_target")
	private Integer issue_id;
    @DatabaseField(uniqueIndexName="watcher_target",
			foreign = true,foreignColumnName="id", columnName= "user_id", foreignAutoRefresh = true)
	private RedmineUser user;
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

	public Integer getIssueId() {
		return issue_id;
	}

	public void setIssueId(Integer issue_id) {
		this.issue_id = issue_id;
	}

	public RedmineUser getUser() {
		return user;
	}

	public void setUser(RedmineUser user) {
		this.user = user;
	}

}
