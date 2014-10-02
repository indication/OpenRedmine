package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DatabaseTable
public class RedmineJournal
		implements IConnectionRecord
		,IPostingRecord
		,IUserRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String JOURNAL_ID = "journal_id";
	public final static String ISSUE_ID = "issue_id";

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField(uniqueIndexName="journal_target",columnName = RedmineConnection.CONNECTION_ID)
	private Integer connection_id;
	@DatabaseField()
	private Long issue_id;
	@DatabaseField(uniqueIndexName="journal_target")
	private int journal_id;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "user_id", foreignAutoRefresh = true)
	private RedmineUser user;
	@DatabaseField
	private String notes;
	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	private byte[] detail;
	@DatabaseField
	private Date created;
	@DatabaseField
	private Date modified;

	public List<RedmineJournalChanges> changes;

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

	@Override
	public void setRedmineConnection(RedmineConnection connection) {
		this.setConnectionId(connection.getId());
	}
	/**
	 * @return journal_id
	 */
	public int getJournalId() {
		return journal_id;
	}
	/**
	 * @param journal_id セットする journal_id
	 */
	public void setJournalId(int journal_id) {
		this.journal_id = journal_id;
	}
	/**
	 * @return detail
	 */
	public byte[] getDetail() {
		return detail;
	}
	/**
	 * @param detail セットする detail
	 */
	public void setDetail(byte[] detail) {
		this.detail = detail;
	}
	public void setDetails(List<RedmineJournalChanges> details) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(details);
		this.detail = baos.toByteArray();
	}
	@SuppressWarnings("unchecked")
	public List<RedmineJournalChanges> getDetails() throws IOException, ClassNotFoundException {
		if(this.detail == null){
			return new ArrayList<RedmineJournalChanges>();
		}
		ByteArrayInputStream baos = new ByteArrayInputStream(this.detail);
		ObjectInputStream oos = new ObjectInputStream(baos);
		return (List<RedmineJournalChanges>)oos.readObject();
	}
	/**
	 * @return notes
	 */
	public String getNotes() {
		return notes;
	}
	/**
	 * @param notes セットする notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
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

	@Override
	public void setConnectionId(Integer connection_id) {
		this.connection_id = connection_id;
	}
	@Override
	public Integer getConnectionId() {
		return connection_id;
	}
	/**
	 * @return issue_id
	 */
	public Long getIssueId() {
		return issue_id;
	}
	/**
	 * @param issue_id セットする issue_id
	 */
	public void setIssueId(Long issue_id) {
		this.issue_id = issue_id;
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
	@Override
	public Element getXml(Document document) {
		Element root = document.createElement("issue");

		if(getNotes() != null){
			Element name = document.createElement("notes");
			name.appendChild(document.createTextNode(this.getNotes()));
			root.appendChild(name);
		} else {
			throw new IllegalArgumentException("RedmineJournal Notes is null.");
		}
		return root;
	}

}
