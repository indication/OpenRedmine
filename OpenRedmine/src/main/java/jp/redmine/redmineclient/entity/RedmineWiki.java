package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DatabaseTable
public class RedmineWiki
		implements IConnectionRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String PROJECT_ID = "project_id";
	public final static String TITLE = "title";
	public final static String NAME = "name";

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField(uniqueIndexName = "wiki_target", columnName = RedmineConnection.CONNECTION_ID)
	private Integer connection_id;
	@DatabaseField(uniqueIndexName = "wiki_target", foreign = true, foreignColumnName = "id", columnName = "project_id", foreignAutoRefresh = true)
	private RedmineProject project;
	@DatabaseField(uniqueIndexName = "wiki_target")
	private String title;
	@DatabaseField
	private int version;
	@DatabaseField
	private String body;
	@DatabaseField
	private String body_html;
	@DatabaseField
	private String parent;
	@DatabaseField(foreign = true,foreignColumnName="id", columnName= "author_id", foreignAutoRefresh = true)
	private RedmineUser author;
	@DatabaseField
	private String comment;
	@DatabaseField
	private Date created;
	@DatabaseField
	private Date modified;
	@DatabaseField
	private Date data_modified;
	//supports for nested sort
	@DatabaseField
	private int rgt;
	@DatabaseField
	private int lft;

	public Date getDataModified() {
		return data_modified;
	}

	public void setDataModified(Date data_modified) {
		this.data_modified = data_modified;
	}

	public List<RedmineAttachment> getAttachments() {
		return (attachments == null) ? new ArrayList<RedmineAttachment>() : attachments;
	}

	public void setAttachments(List<RedmineAttachment> attachments) {
		this.attachments = attachments;
	}

	private List<RedmineAttachment> attachments;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	@Override
	public void setRedmineConnection(RedmineConnection connection) {
		this.setConnectionId(connection.getId());
	}

	public RedmineProject getProject() {
		return project;
	}

	public void setProject(RedmineProject project) {
		this.project = project;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getBodyHtml() {
		return body_html;
	}

	public void setBodyHtml(String body_html) {
		this.body_html = body_html;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public RedmineUser getAuthor() {
		return author;
	}

	public void setAuthor(RedmineUser author) {
		this.author = author;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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
	public void setConnectionId(Integer connection_id) {
		this.connection_id = connection_id;
	}

	@Override
	public Integer getConnectionId() {
		return connection_id;
	}


}
