package jp.redmine.redmineclient.param;

public class WebArgument extends ConnectionArgument {
	public static final String URL = "URL";

	public void setUrl(String url){
		setArg(URL,url);
	}
	public String getUrl(){
		return getArg(URL, "");
	}

	public void importArgument(WebArgument arg) {
		setUrl(arg.getUrl());
		super.importArgument(arg);
	}
}
