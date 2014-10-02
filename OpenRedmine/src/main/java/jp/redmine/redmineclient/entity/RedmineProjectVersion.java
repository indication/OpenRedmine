package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public class RedmineProjectVersion
		implements IConnectionRecord
		,IMasterRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String VERSION_ID = "version_id";
	public final static String DUE_DATE = "due_date";
	public final static String PROJECT_ID = "project_id";
	public final static String NAME = "name";

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(uniqueIndexName="projectversion_target", columnName = RedmineConnection.CONNECTION_ID)
    private Integer connection_id;
    @DatabaseField(foreign = true,foreignColumnName="id"
    	,columnName= "project_id"
    	,foreignAutoRefresh = true)
    private RedmineProject project;
    @DatabaseField(uniqueIndexName="projectversion_target")
    private int version_id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String status;
    @DatabaseField
    private Date due_date;
    @DatabaseField
    private String sharing;
    @DatabaseField
    private String description;
    @DatabaseField
    private Date created;
    @DatabaseField
    private Date modified;


    @Override
    public String toString(){
    	return name;
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
	 * @param name セットする 名称
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return name 名称
	 */
	public String getName() {
		return name;
	}
	@Override
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
	 * @return sharing
	 */
	public String getSharing() {
		return sharing;
	}


	/**
	 * @param sharing セットする sharing
	 */
	public void setSharing(String sharing) {
		this.sharing = sharing;
	}


	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param description セットする description
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @param status セットする status
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	/**
	 * @return status
	 */
	public String getStatus() {
		return status;
	}


	/**
	 * @param version_id セットする version_id
	 */
	public void setVersionId(int version_id) {
		this.version_id = version_id;
	}


	/**
	 * @return version_id
	 */
	public int getVersionId() {
		return version_id;
	}


	@Override
	public void setRemoteId(Long id) {
		if(id == null)
			return;
		setVersionId(id.intValue());
	}


	@Override
	public Long getRemoteId() {
		return (long)getVersionId();
	}

}
