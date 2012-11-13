package jp.redmine.redmineclient.intent;

import android.content.Context;
import android.content.Intent;

public class ConnectionNaviResultIntent extends ConnectionNaviIntent {
	public ConnectionNaviResultIntent(Intent intent) {
		super(intent);
	}
	public ConnectionNaviResultIntent(Context applicationContext,Class<?> cls) {
		super(applicationContext,cls);
	}
	public static final String TOKEN = "TOKEN";
	public static final String ID = "AUTH_ID";
	public static final String PASSWORD = "AUTH_PASSWORD";
	public static final String UNSAFE_SSL = "UNSAFE_SSL";

	public void setToken(String token){
		intent.putExtra(TOKEN,token);
	}
	public String getToken(){
		return intent.getStringExtra(TOKEN);
	}
	public void setUnsafeSSL(boolean ssl){
		intent.putExtra(UNSAFE_SSL,ssl);
	}
	public boolean isUnsafeSSL(){
		return intent.getBooleanExtra(UNSAFE_SSL,false);
	}
}
