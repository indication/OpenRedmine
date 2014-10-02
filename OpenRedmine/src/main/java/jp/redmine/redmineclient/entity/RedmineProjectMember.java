package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public class RedmineProjectMember
		implements IConnectionRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String PROJECT_ID = "project_id";
	public final static String NAME = "name";

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(uniqueIndexName="projectmember_target", columnName = RedmineConnection.CONNECTION_ID)
    private Integer connection_id;
    @DatabaseField(uniqueIndexName="projectmember_target"
    	,foreign = true,foreignColumnName="id"
    	,columnName= "project_id"
    	,foreignAutoRefresh = true)
    private RedmineProject project;
    @DatabaseField(uniqueIndexName="projectmember_target")
    private int category_id;
    @DatabaseField
    private String name;
    @DatabaseField(foreign = true,foreignColumnName="id"
        	,columnName= "assignto_id"
        	,foreignAutoRefresh = true)
    private RedmineUser assignto;
    //unused
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


	/**
	 * @param assignto セットする assignto
	 */
	public void setAssignTo(RedmineUser assignto) {
		this.assignto = assignto;
	}


	/**
	 * @return assignto
	 */
	public RedmineUser getAssignTo() {
		return assignto;
	}


	/**
	 * @param category_id セットする category_id
	 */
	public void setCategoryId(int category_id) {
		this.category_id = category_id;
	}


	/**
	 * @return category_id
	 */
	public int getCategoryId() {
		return category_id;
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
