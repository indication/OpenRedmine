package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.types.DateTimeType;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RedmineProjects {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(uniqueIndexName="target",foreign = true, foreignAutoRefresh = true)
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
    private DateTimeType created;
    @DatabaseField
    private DateTimeType modified;




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
	public void Created(DateTimeType created) {
		this.created = created;
	}
	/**
	 * @return created
	 */
	public DateTimeType Created() {
		return created;
	}
	/**
	 * @param modified セットする modified
	 */
	public void Modified(DateTimeType modified) {
		this.modified = modified;
	}
	/**
	 * @return modified
	 */
	public DateTimeType Modified() {
		return modified;
	}

}
