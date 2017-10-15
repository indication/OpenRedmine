package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import jp.redmine.redmineclient.form.helper.WikiType;

@DatabaseTable
public class RedmineConnection {
	public final static String ID = "id";
	public final static String CONNECTION_ID = "connection_id";
	public final static int TEXT_TYPE_TEXTTILE = 0;
	public final static int TEXT_TYPE_MARKDOWN = 1;
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
    @DatabaseField
    private boolean permitunsafe;
    @DatabaseField
    private String certkey;
	@DatabaseField
	private int text_type;

    @Override
    public String toString(){
    	return name;
    }

    ////////////////////////////////////////////////////////

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setNowarn(boolean nowarn) {
		this.nowarn = nowarn;
	}

	public boolean getNowarn() {
		return nowarn;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuthId(String authid) {
		this.authid = authid;
	}

	public String getAuthId() {
		return authid;
	}

	public void setAuthPasswd(String authpass) {
		this.authpass = authpass;
	}

	public String getAuthPasswd() {
		return authpass;
	}

	public void setPermitUnsafe(boolean permitunsafe) {
		this.permitunsafe = permitunsafe;
	}

	public boolean isPermitUnsafe() {
		return permitunsafe;
	}

	public void setCertKey(String certkey) {
		this.certkey = certkey;
	}

	public String getCertKey() {
		return certkey;
	}


	public int getTextType() {
		return text_type;
	}
	public WikiType getWikiType() {
		switch (text_type){
			case TEXT_TYPE_TEXTTILE:
				return WikiType.Texttile;
			case TEXT_TYPE_MARKDOWN:
				return WikiType.Markdown;
			default:
				return WikiType.None;
		}
	}

	public void setTextType(int text_type) {
		this.text_type = text_type;
	}

}
