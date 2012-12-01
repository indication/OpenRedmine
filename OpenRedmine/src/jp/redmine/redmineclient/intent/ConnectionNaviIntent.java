package jp.redmine.redmineclient.intent;

import android.content.Context;
import android.content.Intent;

public class ConnectionNaviIntent extends CoreIntent {
	public ConnectionNaviIntent(Intent intent) {
		super(intent);
	}
	public ConnectionNaviIntent(Context applicationContext,Class<?> cls) {
		super(applicationContext,cls);
	}
	public static final String URL = "URL";
	public static final String ID = "AUTH_ID";
	public static final String PASSWORD = "AUTH_PASSWORD";

	public void setUrl(String id){
		intent.putExtra(URL,id);
	}
	public String getUrl(){
		return intent.getStringExtra(URL);
	}
	public void setAuthID(String id){
		intent.putExtra(ID,id);
	}
	public String getAuthID(){
		return intent.getStringExtra(ID);
	}
	public void setAuthPassword(String pass){
		intent.putExtra(PASSWORD,pass);
	}
	public String getAuthPassword(){
		return intent.getStringExtra(PASSWORD);
	}
}
