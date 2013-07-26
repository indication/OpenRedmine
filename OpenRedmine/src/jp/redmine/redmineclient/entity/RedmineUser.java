package jp.redmine.redmineclient.entity;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RedmineUser implements IMasterRecord {
	public final static String ID = "id";
	public final static String CONNECTION = "connection_id";
	public final static String USER_ID = "user_id";
	public final static String NAME = "name";
	public final static int STATUS_ANONYMOUS		= 0;
	public final static int STATUS_ACTIVE			= 1;
	public final static int STATUS_REGISTERED		= 2;
	public final static int STATUS_LOCKED			= 3;


    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(uniqueIndexName="user_target")
    private Integer connection_id;
    @DatabaseField(uniqueIndexName="user_target")
    private Integer user_id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String loginname;
    @DatabaseField
    private String mail;
    @DatabaseField
    private Date created;
    @DatabaseField
    private Date modified;
    @DatabaseField
    private boolean is_current;
    private String firstname;
    private String lastname;


    @Override
    public String toString(){
    	return "";
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
	 * @param connection_id セットする connection_id
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
	 * @param user_id セットする user_id
	 */
	public void setUserId(Integer user_id) {
		this.user_id = user_id;
	}


	/**
	 * @return user_id
	 */
	public Integer getUserId() {
		return user_id;
	}


	/**
	 * @param name セットする name
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param mail セットする mail
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}


	/**
	 * @return mail
	 */
	public String getMail() {
		return mail;
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


	/**
	 * @param loginname セットする loginname
	 */
	public void setLoginName(String loginname) {
		this.loginname = loginname;
	}


	/**
	 * @return loginname
	 */
	public String getLoginName() {
		return loginname;
	}


	public String getFirstname() {
		return firstname;
	}


	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	public String getLastname() {
		return lastname;
	}


	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return is_current
	 */
	public boolean isCurrent() {
		return is_current;
	}


	/**
	 * @param is_current セットする is_current
	 */
	public void setIsCurrent(boolean is_current) {
		this.is_current = is_current;
	}


	@Override
	public void setRemoteId(Long id) {
		setUserId((id==null)? null : id.intValue());
	}


	@Override
	public Long getRemoteId() {
		return (long)getUserId();
	}

}
