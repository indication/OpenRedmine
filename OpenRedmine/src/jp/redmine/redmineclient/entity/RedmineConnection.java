package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RedmineConnection {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String url;
    @DatabaseField
    private boolean nowarn;
    @DatabaseField
    private String token;
    @DatabaseField
    private boolean auth;
    @DatabaseField
    private String authid;
    @DatabaseField
    private String authpass;

    @Override
    public String toString(){
    	return name;
    }

    ////////////////////////////////////////////////////////
    /**
     * IDを設定
     * @param id ID
     */
	public void Id(Integer id) {
		this.id = id;
	}
	/**
	 * IDを取得
	 * @return ID
	 */
	public Integer Id() {
		return id;
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
	 * @param url セットする url
	 */
	public void Url(String url) {
		this.url = url;
	}
	/**
	 * @return url
	 */
	public String Url() {
		return url;
	}
	/**
	 * @param nowarn セットする nowarn
	 */
	public void Nowarn(boolean nowarn) {
		this.nowarn = nowarn;
	}
	/**
	 * @return nowarn
	 */
	public boolean Nowarn() {
		return nowarn;
	}
	/**
	 * @param token セットする token
	 */
	public void Token(String token) {
		this.token = token;
	}
	/**
	 * @return token
	 */
	public String Token() {
		return token;
	}
	/**
	 * @param auth セットする auth
	 */
	public void Auth(boolean auth) {
		this.auth = auth;
	}
	/**
	 * @return auth
	 */
	public boolean Auth() {
		return auth;
	}
	/**
	 * @param authid セットする authid
	 */
	public void AuthId(String authid) {
		this.authid = authid;
	}
	/**
	 * @return authid
	 */
	public String AuthId() {
		return authid;
	}
	/**
	 * @param authpass セットする authpass
	 */
	public void AuthPasswd(String authpass) {
		this.authpass = authpass;
	}
	/**
	 * @return authpass
	 */
	public String AuthPasswd() {
		return authpass;
	}

}
