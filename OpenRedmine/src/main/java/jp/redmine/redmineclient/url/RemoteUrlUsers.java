package jp.redmine.redmineclient.url;

import java.util.HashMap;
import java.util.Map.Entry;

import android.net.Uri;

public class RemoteUrlUsers extends RemoteUrl {
	private HashMap<String,String> params = new HashMap<String,String>();
	private boolean isCurrent = false;

	public void filterId(int id){
		params.put("id", Integer.toString(id));
	}
	public void filterCurrentUser(){
		filterCurrentUser(true);
	}
	public void filterCurrentUser(boolean current){
		isCurrent = current;
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		StringBuilder sb = new StringBuilder();
		sb.append(isCurrent ? "users/current" : "users");
		sb.append(".");
		sb.append(getExtension());
		url.appendEncodedPath(sb.toString());
		for(Entry<String,String> data : params.entrySet()){
			if(data.getValue() != null){
				url.appendQueryParameter(data.getKey(), data.getValue());
			}
		}
		return url;
	}
}
