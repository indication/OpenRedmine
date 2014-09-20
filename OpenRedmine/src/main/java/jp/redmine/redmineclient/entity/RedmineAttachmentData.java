package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public class RedmineAttachmentData
	implements IConnectionRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String ATTACHMENT_ID = "attachment_id";

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField(indexName="attachment_data_target", columnName = RedmineConnection.CONNECTION_ID)
	private Integer connection_id;
	@DatabaseField(indexName = "attachment_data_target")
	private int attachment_id;
	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	private byte[] data;
	@DatabaseField
	private int data_size;
	@DatabaseField
	private Date created;

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

	public void setAttachemnt(RedmineAttachment attachment){
		setConnectionId(attachment.getConnectionId());
		setAttachmentId(attachment.getAttachmentId());
	}

	@Override
	public void setConnectionId(Integer connection_id) {
		this.connection_id = connection_id;
	}
	@Override
	public Integer getConnectionId() {
		return connection_id;
	}

	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}

	public int getSize() {
		return data_size;
	}
	public void setSize(int data_size) {
		this.data_size = data_size;
	}

}
