package jp.redmine.redmineclient.url;

import java.util.HashMap;
import java.util.Map.Entry;

import android.net.Uri;

public class RemoteUrlUsers extends RemoteUrl {
	private HashMap<String,String> params = new HashMap<String,String>();

	@Override
	public versions getMinVersion(){
		return versions.v130;
	}
	public void filterId(int id){
		params.put("id", Integer.toString(id));
	}
	public void filterCurrentUser(){
		params.put("id", "current");
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("users." + getExtention());
		for(Entry<String,String> data : params.entrySet()){
			if(data.getValue() != null){
				url.appendQueryParameter(data.getKey(), data.getValue());
			}
		}
		return url;
	}
}
