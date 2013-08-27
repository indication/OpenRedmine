package jp.redmine.redmineclient.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RedmineIssueRelation {
	public final static String ID = "id";
	public final static String CONNECTION = "connection_id";
	public final static String RELATION_ID = "relation_id";
	public final static String ISSUE_ID = "issue_id";

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField(uniqueIndexName="relation_target")
	private Integer connection_id;
	@DatabaseField(uniqueIndexName="relation_target")
	private int relation_id;
	@DatabaseField
	private int issue_id;
	@DatabaseField
	private int issue_to_id;
	@DatabaseField
	private BigDecimal delay;
	@DatabaseField(dataType = DataType.ENUM_INTEGER, unknownEnumName = "Relates")
	private RelationType type;
	@DatabaseField
	private Date created;
	@DatabaseField
	private Date modified;
	
	public enum RelationType {
		None		(""				, false,	false	),
		Relates		("relates"		, false,	true	),
		Duplicates	("duplicates"	, false,	true	),
		Duplicated	("duplicated"	, false,	false	),
		Blocks		("blocks"		, false,	true	),
		Blocked		("blocked"		, false,	false	),
		Precedes	("precedes"		, true,		true	),
		Follows		("follows"		, true,		false	),
		;
		
		RelationType(String nm, boolean d, boolean ft){
			this.name = nm;
			this.delay = d;
			this.from_to = ft;
		}
		private String name;
		private boolean delay;
		private boolean from_to;
		public String getName(){
			return name;
		}
		public boolean isDelay(){
			return delay;
		}
		public boolean isFromTo(){
			return from_to;
		}
		
		public static RelationType getValueOf(String name){
			for(RelationType i : values()){
				if(i.getName().equalsIgnoreCase(name))
					return i;
			}
			return null;
		}
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
	 * @param connection セットする connection
	 */
	public void setRedmineConnection(RedmineConnection connection) {
		this.setConnectionId(connection.getId());
	}
	public int getRelationId() {
		return relation_id;
	}
	public void setRelationId(int relation_id) {
		this.relation_id = relation_id;
	}

	public int getIssueId() {
		return issue_id;
	}
	public void setIssueId(int issue_id) {
		this.issue_id = issue_id;
	}

	public int getIssueToId() {
		return issue_to_id;
	}
	public void setIssueToId(int issue_to_id) {
		this.issue_to_id = issue_to_id;
	}

	public BigDecimal getDelay() {
		return delay;
	}
	public void setDelay(BigDecimal delay) {
		this.delay = delay;
	}
	public RelationType getType() {
		return type;
	}
	public void setType(RelationType relationType) {
		this.type = relationType;
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
