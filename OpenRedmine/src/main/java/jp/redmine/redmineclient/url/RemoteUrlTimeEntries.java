package jp.redmine.redmineclient.url;

import java.util.HashMap;
import java.util.Map.Entry;

import android.net.Uri;

public class RemoteUrlTimeEntries extends RemoteUrl {
	private HashMap<String,String> params = new HashMap<String,String>();
	private Integer timeentry_id;

	//:project, :activity, :user, {:issue => :tracker}
	public void filterIssue(String status){
		params.put("issue_id", status);
	}
	public void filterProject(String status){
		params.put("project_id", status);
	}
	public void filterUser(String status){
		params.put("user_id", status);
	}
	public void filterLimit(int limit){
		params.put("limit", Integer.toString(limit));
	}
	public void filterOffset(int offset){
		params.put("offset", Integer.toString(offset));
	}
	public void setId(Integer id){
		timeentry_id = id;
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		if(timeentry_id == null){
			url.appendEncodedPath("time_entries." + getExtension());
		} else {
			url.appendEncodedPath("time_entries/"+String.valueOf(timeentry_id)+"."+ getExtension());
		}
		for(Entry<String,String> data : params.entrySet()){
			if(data.getValue() != null){
				url.appendQueryParameter(data.getKey(), data.getValue());
			}
		}
		return url;
	}
}
