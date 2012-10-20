package jp.redmine.redmineclient.entity;

import java.util.Date;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RedmineJournal {
	public final static String ID = "id";
	public final static String CONNECTION = "connection_id";
	public final static String JOURNAL_ID = "journal_id";

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField(uniqueIndexName="journal_target")
	private Integer connection_id;
	@DatabaseField(uniqueIndexName="journal_target")
	private int issue_id;
	@DatabaseField(uniqueIndexName="journal_target")
	private int journnal_id;
	@DatabaseField
	private int note_id;
	@DatabaseField
	private String content;
	@DatabaseField
	private Date created;
	@DatabaseField
	private Date modified;


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
	 * @param connection セットする connection
	 */
	public void setRedmineConnection(RedmineConnection connection) {
		this.setConnectionId(connection.getId());
	}
	/**
	 * @return journnal_id
	 */
	public int getJournnalId() {
		return journnal_id;
	}
	/**
	 * @param journnal_id セットする journnal_id
	 */
	public void setJournnalId(int journnal_id) {
		this.journnal_id = journnal_id;
	}
	/**
	 * @return content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content セットする content
	 */
	public void setContent(String content) {
		this.content = content;
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
	 * @param connection_id セットする connection_id
	 */
	public void setConnectionId(Integer connection_id) {
		this.connection_id = connection_id;
	}


	/**
	 * @return connection_id
	 */
	public Integer getConnectionId() {
		return connection_id;
	}
	/**
	 * @return issue_id
	 */
	public int getIssueId() {
		return issue_id;
	}
	/**
	 * @param issue_id セットする issue_id
	 */
	public void setIssueId(int issue_id) {
		this.issue_id = issue_id;
	}
	/**
	 * @return note_id
	 */
	public int getNoteId() {
		return note_id;
	}
	/**
	 * @param note_id セットする note_id
	 */
	public void setNoteId(int note_id) {
		this.note_id = note_id;
	}

}
