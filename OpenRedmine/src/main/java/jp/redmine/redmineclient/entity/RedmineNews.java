package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public class RedmineNews
		implements IConnectionRecord
		,IUserRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String PROJECT = "project_id";
	public final static String NEWS_ID = "news_id";

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField(uniqueIndexName="news_target", columnName = RedmineConnection.CONNECTION_ID)
	private Integer connection_id;
	@DatabaseField(uniqueIndexName="news_target")
	private int news_id;
	@DatabaseField(foreign = true,foreignColumnName="id", columnName= "project_id", foreignAutoRefresh = true)
	private RedmineProject project;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "author_id", foreignAutoRefresh = true)
	private RedmineUser author;
	@DatabaseField
	private String summary;
	@DatabaseField
	private String title;
	@DatabaseField
	private String description;
	@DatabaseField
	private Date created;
	@DatabaseField
	private Date modified;
	@DatabaseField
	private Date data_modified;

	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	////////////////////////////////////////////////////////

	public int getNewsId() {
		return news_id;
	}
	public void setNewsId(int news_id) {
		this.news_id = news_id;
	}


	public RedmineProject getProject() {
		return project;
	}
	public void setProject(RedmineProject project) {
		this.project = project;
	}

	public RedmineUser getUser() {
		return author;
	}
	public void setUser(RedmineUser author) {
		this.author = author;
	}

	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public void setRedmineConnection(RedmineConnection connection) {
		this.setConnectionId(connection.getId());
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

	public Date getDataModified() {
		return data_modified;
	}
	public void setDataModified(Date data_modified) {
		this.data_modified = data_modified;
	}

	@Override
	public void setConnectionId(Integer connection_id) {
		this.connection_id = connection_id;
	}
	@Override
	public Integer getConnectionId() {
		return connection_id;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
