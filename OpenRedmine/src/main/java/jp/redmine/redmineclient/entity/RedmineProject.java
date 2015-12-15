package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import jp.redmine.redmineclient.R;

@DatabaseTable
public class RedmineProject
		implements IConnectionRecord
		,IMasterRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String PROJECT_ID = "project_id";
	public final static String NAME = "name";
	public final static String FAVORITE = "favorite";

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(uniqueIndexName="project_target", columnName = RedmineConnection.CONNECTION_ID)
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
    @DatabaseField
    private Integer parent;
	@DatabaseField(dataType = DataType.ENUM_INTEGER, unknownEnumName = "None")
	private Status status;

	public enum Status {
		None		(0	, true,	R.string.project_status_none),
		Active		(1	, true,	R.string.project_status_active),
		Closed		(2	, false,R.string.project_status_closed),
		Archived	(9	, false,R.string.project_status_archived),
		;

		Status(int nm, boolean u, int r){
			this.kind = nm;
			this.res = r;
			this.isUpdateable = u;
		}
		private int kind;
		private int res;

		private boolean isUpdateable;
		public int getKind(){
			return kind;
		}
		public int getResourceId(){
			return res;
		}
		public boolean isUpdateable() {
			return isUpdateable;
		}

		public static Status getValueOf(int kind){
			for(Status i : values()){
				if(i.getKind() == kind)
					return i;
			}
			return null;
		}
	}

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

	@Override
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
		return favorite == null ? 0 : favorite;
	}

	/**
	 * @return parent
	 */
	public Integer getParent() {
		return parent;
	}

	/**
	 * @param parent セットする parent
	 */
	public void setParent(Integer parent) {
		this.parent = parent;
	}

	public Status getStatus() {
		return status == null ? Status.None : status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public void setConnectionId(Integer connection_id) {
		this.connection_id = connection_id;
	}
	@Override
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
