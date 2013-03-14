package jp.redmine.redmineclient.entity;

import java.util.Date;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RedmineTimeActivity implements IMasterRecord {
	public final static String ID = "id";
	public final static String CONNECTION = "connection_id";
	public final static String ACTIVITY_ID = "activity_id";

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(uniqueIndexName="activity_target")
    private Integer connection_id;
    @DatabaseField(uniqueIndexName="activity_target")
    private Integer activity_id;
    @DatabaseField
    private String name;
    @DatabaseField
    private Date created;
    @DatabaseField
    private Date modified;
    @DatabaseField(defaultValue="false")
    private boolean is_default;


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
	 * @return activity_id
	 */
	public Integer getActivityId() {
		return activity_id;
	}
	/**
	 * @param activity_id セットする activity_id
	 */
	public void setActivityId(Integer activity_id) {
		this.activity_id = activity_id;
	}
	/**
	 * @return name
	 */
	@Override
	public String getName() {
		return name;
	}
	/**
	 * @param name セットする name
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param connection セットする connection
	 */
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
	 * @return is_default
	 */
	public boolean isDefault() {
		return is_default;
	}
	/**
	 * @param is_default セットする is_default
	 */
	public void setDefault(boolean is_default) {
		this.is_default = is_default;
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
	@Override
	public void setRemoteId(Long id) {
		if(id == null)
			return;
		setActivityId(id.intValue());
	}


	@Override
	public Long getRemoteId() {
		return (getActivityId() == null) ? null : (long)getActivityId();
	}

}
