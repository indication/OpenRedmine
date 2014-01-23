package jp.redmine.redmineclient.param;

public class ConnectionNaviArgument extends Core {
	public static final String URL = "URL";
	public static final String ID = "AUTH_ID";
	public static final String PASSWORD = "AUTH_PASSWORD";

	public void setUrl(String id){
		setArg(URL,id);
	}
	public String getUrl(){
		return getArg(URL,(String)null);
	}
	public void setAuthID(String id){
		setArg(ID,id);
	}
	public String getAuthID(){
		return getArg(ID,(String)null);
	}
	public void setAuthPassword(String pass){
		setArg(PASSWORD,pass);
	}
	public String getAuthPassword(){
		return getArg(PASSWORD,(String)null);
	}

	public void importArgument(ConnectionNaviArgument arg) {
		setUrl(arg.getUrl());
		setAuthID(arg.getAuthID());
		setAuthPassword(arg.getAuthPassword());
		super.importArgument(arg);
	}
}
