package jp.redmine.redmineclient.entity;

import java.util.Date;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RedmineProject implements IMasterRecord {
	public final static String ID = "id";
	public final static String CONNECTION = "connection_id";
	public final static String PROJECT_ID = "project_id";
	public final static String NAME = "name";

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(uniqueIndexName="project_target")
    private Integer connection_id;
    @DatabaseField(uniqueIndexName="project_target")
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
    @DatabaseField
    private Integer sort_order;
    @DatabaseField
    private Integer favorite;


    @Override
    public String toString(){
    	return name;
    }

    public void setForeginData(RedmineProject item){
    	project_id = item.getProjectId();
		name = item.getName();
		identifier = item.getIdentifier();
		description = item.getDescription();
		homepage = item.getHomepage();
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
     * IDを設定
     * @param id ID
     */
	public void setProjectId(Integer id) {
		this.project_id = id;
	}
	/**
	 * IDを取得
	 * @return ID
	 */
	public Integer getProjectId() {
		return project_id;
	}
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
	public void setRedmineConnection(RedmineConnection connection) {
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
	 * @param sort_order セットする sort_order
	 */
	public void setSortOrder(Integer sort_order) {
		this.sort_order = sort_order;
	}


	/**
	 * @return sort_order
	 */
	public Integer getSortOrder() {
		return sort_order;
	}


	/**
	 * @param favorite セットする favorite
	 */
	public void setFavorite(Integer favorite) {
		this.favorite = favorite;
	}


	/**
	 * @return favorite
	 */
	public Integer getFavorite() {
		return favorite;
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
		setProjectId((id==null)? null : id.intValue());
	}


	@Override
	public Long getRemoteId() {
		return (long)getProjectId();
	}
}
