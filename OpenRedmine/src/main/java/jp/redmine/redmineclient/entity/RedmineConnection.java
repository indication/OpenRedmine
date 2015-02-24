package jp.redmine.redmineclient.entity;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation;

import jp.redmine.redmineclient.BuildConfig;

@AdditionalAnnotation.Contract()
@DatabaseTable
@AdditionalAnnotation.DefaultContentUri(authority = BuildConfig.APPLICATION_ID, path = "connection")
@AdditionalAnnotation.DefaultContentMimeTypeVnd(name = BuildConfig.PROVIDER_ID, type = "connection")
public class RedmineConnection {
	public final static String ID = "id";
	public final static String CONNECTION_ID = "connection_id";
    @DatabaseField(columnName = BaseColumns._ID,generatedId = true)
	@AdditionalAnnotation.DefaultSortOrder
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
    @DatabaseField
    private boolean permitunsafe;
    @DatabaseField
    private String certkey;

    @Override
    public String toString(){
    	return name;
    }

    ////////////////////////////////////////////////////////
    /**
     * IDを設定
     * @param id ID
     */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * IDを取得
	 * @return ID
	 */
	public Integer getId() {
		return id;
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
	 * @param url セットする url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param nowarn セットする nowarn
	 */
	public void setNowarn(boolean nowarn) {
		this.nowarn = nowarn;
	}
	/**
	 * @return nowarn
	 */
	public boolean getNowarn() {
		return nowarn;
	}
	/**
	 * @param token セットする token
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param auth セットする auth
	 */
	public void setAuth(boolean auth) {
		this.auth = auth;
	}
	/**
	 * @return auth
	 */
	public boolean isAuth() {
		return auth;
	}
	/**
	 * @param authid セットする authid
	 */
	public void setAuthId(String authid) {
		this.authid = authid;
	}
	/**
	 * @return authid
	 */
	public String getAuthId() {
		return authid;
	}
	/**
	 * @param authpass セットする authpass
	 */
	public void setAuthPasswd(String authpass) {
		this.authpass = authpass;
	}
	/**
	 * @return authpass
	 */
	public String getAuthPasswd() {
		return authpass;
	}

	/**
	 * @param permitunsafe セットする permitunsafe
	 */
	public void setPermitUnsafe(boolean permitunsafe) {
		this.permitunsafe = permitunsafe;
	}

	/**
	 * @return permitunsafe
	 */
	public boolean isPermitUnsafe() {
		return permitunsafe;
	}

	/**
	 * @param certkey セットする certkey
	 */
	public void setCertKey(String certkey) {
		this.certkey = certkey;
	}

	/**
	 * @return certkey
	 */
	public String getCertKey() {
		return certkey;
	}

	public static RedmineConnection getByCursor(Cursor c){
		RedmineConnection item = new RedmineConnection();
		item.setId			(c.getInt	(c.getColumnIndex(RedmineConnectionContract._ID			)));
		item.setName		(c.getString(c.getColumnIndex(RedmineConnectionContract.NAME		)));
		item.setUrl			(c.getString(c.getColumnIndex(RedmineConnectionContract.URL			)));
		item.setNowarn		(c.getInt	(c.getColumnIndex(RedmineConnectionContract.NOWARN		)) != 0);
		item.setToken		(c.getString(c.getColumnIndex(RedmineConnectionContract.TOKEN		)));
		item.setAuth		(c.getInt	(c.getColumnIndex(RedmineConnectionContract.AUTH		)) != 0);
		item.setAuthId		(c.getString(c.getColumnIndex(RedmineConnectionContract.AUTHID		)));
		item.setAuthPasswd	(c.getString(c.getColumnIndex(RedmineConnectionContract.AUTHPASS	)));
		item.setPermitUnsafe(c.getInt	(c.getColumnIndex(RedmineConnectionContract.PERMITUNSAFE)) != 0);
		item.setCertKey		(c.getString(c.getColumnIndex(RedmineConnectionContract.CERTKEY		)));
		return item;
	}
}
