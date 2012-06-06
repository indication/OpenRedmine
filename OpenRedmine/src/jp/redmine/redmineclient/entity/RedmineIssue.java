package jp.redmine.redmineclient.entity;

import java.util.Date;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RedmineIssue {
	public final static String ID = "id";
	public final static String CONNECTION = "connection_id";
	public final static String PROJECT_ID = "project_id";
	public final static String ISSUE_ID = "project_id";
	public final static String NAME = "name";

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(uniqueIndexName="project_target")
    private Integer connection_id;
    @DatabaseField(uniqueIndexName="project_target",foreign = true,foreignColumnName="id", columnName= "project_id", foreignAutoRefresh = true)
    private RedmineProject project;
    @DatabaseField
    private String name;
    @DatabaseField
    private String identifier;
    @DatabaseField
    private String description;
    @DatabaseField
    private String homepage;
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
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return id
	 */
	public Integer getId() {
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
	/**
	 * @param identifier セットする Identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	/**
	 * @return identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * @param description セットする description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param homepage セットする homepage
	 */
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	/**
	 * @return homepage
	 */
	public String getHomepage() {
		return homepage;
	}
	/**
	 * @param connection セットする connection
	 */
	public void RedmineConnection(RedmineConnection connection) {
		this.connection_id = connection.getId();
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

}
