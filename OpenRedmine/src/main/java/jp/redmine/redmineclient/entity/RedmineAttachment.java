package jp.redmine.redmineclient.entity;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public class RedmineAttachment
		implements IConnectionRecord
		,IUserRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String ATTACHMENT_ID = "attachment_id";
	public final static String ISSUE_ID = "issue_id";

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField(uniqueIndexName="attachment_target", columnName = RedmineConnection.CONNECTION_ID)
	private Integer connection_id;
	@DatabaseField()
	private Integer issue_id;
	@DatabaseField
	private Long wiki_id;
	@DatabaseField(uniqueIndexName="attachment_target")
	private int attachment_id;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "user_id", foreignAutoRefresh = true)
	private RedmineUser user;
	@DatabaseField
	private String filename;
	@DatabaseField
	private int filesize;
	@DatabaseField
	private String description;
	@DatabaseField
	private String content_type;
	@DatabaseField
	private String content_url;
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
	////////////////////////////////////////////////////////

	@Override
	public void setRedmineConnection(RedmineConnection connection) {
		this.setConnectionId(connection.getId());
	}
	public int getAttachmentId() {
		return attachment_id;
	}
	public void setAttachmentId(int attachment_id) {
		this.attachment_id = attachment_id;
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

	public Integer getIssueId() {
		return issue_id;
	}
	public void setIssueId(Integer issue_id) {
		this.issue_id = issue_id;
	}
	public Long getWikiId() {
		return wiki_id;
	}
	public void setWikiId(Long wiki_id) {
		this.wiki_id = wiki_id;
	}
	public RedmineUser getUser() {
		return user;
	}
	public void setUser(RedmineUser user) {
		this.user = user;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getFilesize() {
		return filesize;
	}
	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getContentType() {
		return content_type;
	}
	public void setContentType(String content_type) {
		this.content_type = content_type;
	}
	public String getContentUrl() {
		return content_url;
	}
	public void setContentUrl(String content_url) {
		this.content_url = content_url;
	}
	public String getLocalFileName(){
		return String.valueOf(connection_id) + "_" + String.valueOf(id);
	}
	@SuppressLint("DefaultLocale")
	public String getFilenameExt(){
		int ch = TextUtils.isEmpty(getFilename()) ? -1 : getFilename().lastIndexOf('.');
		return ((ch>=0)?getFilename().substring(ch + 1):"").toLowerCase();
	}

}
