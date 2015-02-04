package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;
import java.util.Date;

import jp.redmine.redmineclient.R;

@DatabaseTable
public class RedmineIssueRelation
		implements IConnectionRecord
{
	public final static String ID = "id";
	public final static String CONNECTION = RedmineConnection.CONNECTION_ID;
	public final static String RELATION_ID = "relation_id";
	public final static String ISSUE_ID = "issue_id";
	public final static String ISSUE_TO_ID = "issue_to_id";

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField(uniqueIndexName="relation_target", columnName = RedmineConnection.CONNECTION_ID)
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
	
	private RedmineIssue issue;
	
	public enum RelationType {
		None		(""				, false,	false,	R.string.relation_none),
		Relates		("relates"		, false,	true,	R.string.relation_relates),
		Duplicates	("duplicates"	, false,	true,	R.string.relation_duplicates),
		Duplicated	("duplicated"	, false,	false,	R.string.relation_duplicated),
		Blocks		("blocks"		, false,	true,	R.string.relation_blocks),
		Blocked		("blocked"		, false,	false,	R.string.relation_blocked),
		Precedes	("precedes"		, true,		true,	R.string.relation_precedes),
		Follows		("follows"		, true,		false,	R.string.relation_follows),
		Copied		("copied_to"	, true,		false,	R.string.relation_copied_to),
		;
		
		RelationType(String nm, boolean d, boolean ft, int r){
			this.name = nm;
			this.delay = d;
			this.from_to = ft;
			this.res = r;
		}
		private String name;
		private boolean delay;
		private boolean from_to;
		private int res;
		public String getName(){
			return name;
		}
		public boolean isDelay(){
			return delay;
		}
		public boolean isFromTo(){
			return from_to;
		}
		public int getResourceId(){
			return res;
		}
		
		public static RelationType getValueOf(String name){
			for(RelationType i : values()){
				if(i.getName().equalsIgnoreCase(name))
					return i;
			}
			return None;
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
	@Override
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


	@Override
	public void setConnectionId(Integer connection_id) {
		this.connection_id = connection_id;
	}
	@Override
	public Integer getConnectionId() {
		return connection_id;
	}

	public RedmineIssue getIssue() {
		return issue;
	}
	public void setIssue(RedmineIssue issue) {
		this.issue = issue;
	}

	public int getTargetIssueId(int from_id){
		return from_id == issue_id ? issue_to_id : issue_id;
	}

}
