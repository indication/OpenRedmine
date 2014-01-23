package jp.redmine.redmineclient.param;

public class ConnectionNaviResultArgument extends ConnectionNaviArgument {
	public static final String TOKEN = "TOKEN";
	public static final String ID = "AUTH_ID";
	public static final String PASSWORD = "AUTH_PASSWORD";
	public static final String UNSAFE_SSL = "UNSAFE_SSL";

	public void setToken(String token){
		setArg(TOKEN,token);
	}
	public String getToken(){
		return getArg(TOKEN,(String)null);
	}
	public void setUnsafeSSL(boolean ssl){
		setArg(UNSAFE_SSL,ssl);
	}
	public boolean isUnsafeSSL(){
		return getArg(UNSAFE_SSL,false);
	}

	public void importArgument(ConnectionNaviResultArgument arg) {
		setToken(arg.getToken());
		setUnsafeSSL(arg.isUnsafeSSL());
		super.importArgument(arg);
	}
}
