package jp.redmine.redmineclient.url;

import java.util.HashMap;
import java.util.Map.Entry;

import android.net.Uri;

public class RemoteUrlTimeEntries extends RemoteUrl {
	private HashMap<String,String> params = new HashMap<String,String>();

	public void filterIssue(String status){
		params.put("issue_id", status);
	}
	public void filterProject(String status){
		params.put("project_id", status);
	}
	public void filterLimit(int limit){
		params.put("limit", Integer.toString(limit));
	}
	public void filterOffset(int offset){
		params.put("offset", Integer.toString(offset));
	}
	@Override
	public versions getMinVersion(){
		return versions.v110;
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("tile_entries." + getExtention());
		for(Entry<String,String> data : params.entrySet()){
			if(data.getValue() != null){
				url.appendQueryParameter(data.getKey(), data.getValue());
			}
		}
		return url;
	}
}
