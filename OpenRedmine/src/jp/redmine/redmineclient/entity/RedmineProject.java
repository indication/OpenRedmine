package jp.redmine.redmineclient.entity;

import java.util.Date;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RedmineProject {
	public final static String ID = "id";
	public final static String CONNECTION = "connection_id";
	public final static String PROJECT_ID = "project_id";
	public final static String NAME = "name";

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(uniqueIndexName="target",foreign = true,foreignColumnName="id", columnName= "connection_id", foreignAutoRefresh = true)
    private RedmineConnection connection;
    @DatabaseField(uniqueIndexName="target")
    private Integer project_id;
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
	public void Id(Integer id) {
		this.id = id;
	}
	/**
	 * @return id
	 */
	public Integer Id() {
		return id;
	}
	////////////////////////////////////////////////////////
    /**
     * IDを設定
     * @param id ID
     */
	public void ProjectId(Integer id) {
		this.project_id = id;
	}
	/**
	 * IDを取得
	 * @return ID
	 */
	public Integer ProjectId() {
		return project_id;
	}
	/**
	 * @param name セットする 名称
	 */
	public void Name(String name) {
		this.name = name;
	}
	/**
	 * @return name 名称
	 */
	public String Name() {
		return name;
	}
	/**
	 * @param identifier セットする Identifier
	 */
	public void Identifier(String identifier) {
		this.identifier = identifier;
	}
	/**
	 * @return identifier
	 */
	public String Identifier() {
		return identifier;
	}
	/**
	 * @param description セットする description
	 */
	public void Description(String description) {
		this.description = description;
	}
	/**
	 * @return description
	 */
	public String Description() {
		return description;
	}
	/**
	 * @param homepage セットする homepage
	 */
	public void Homepage(String homepage) {
		this.homepage = homepage;
	}
	/**
	 * @return homepage
	 */
	public String Homepage() {
		return homepage;
	}
	/**
	 * @param connection セットする connection
	 */
	public void RedmineConnection(RedmineConnection connection) {
		this.connection = connection;
	}
	/**
	 * @return connection
	 */
	public RedmineConnection RedmineConnection() {
		return connection;
	}
	/**
	 * @param created セットする created
	 */
	public void Created(Date created) {
		this.created = created;
	}
	/**
	 * @return created
	 */
	public Date Created() {
		return created;
	}
	/**
	 * @param modified セットする modified
	 */
	public void Modified(Date modified) {
		this.modified = modified;
	}
	/**
	 * @return modified
	 */
	public Date Modified() {
		return modified;
	}

}
