package jp.redmine.redmineclient.url;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import android.net.Uri;
import android.text.format.DateFormat;

public class RemoteUrlIssues extends RemoteUrl {
	private HashMap<String,String> params = new HashMap<String,String>();

	public void filterLimit(int limit){
		params.put("limit", Integer.toString(limit));
	}
	public void filterOffset(int offset){
		params.put("offset", Integer.toString(offset));
	}

	public void filterStatus(String status){
		params.put("status_id", status);
	}

	public void filterProject(String status){
		params.put("project_id", status);
	}
	public void filterCreated(Date from,Date to){
		filterDate("created_on",from,to);
	}
	public void filterModified(Date from,Date to){
		filterDate("modified_on",from,to);
	}

	public void filterDate(String key,Date from,Date to){
		StringBuilder sb = new StringBuilder();
		if(from == null && to != null){
			sb.append("<");
			sb.append(DateFormat.format("YYYY-mm-dd", to));
		} else if(from != null && to == null){
			sb.append(">");
			sb.append(DateFormat.format("YYYY-mm-dd", from));
		} else if(from != null && to != null){
			sb.append("<>");
			sb.append(DateFormat.format("YYYY-mm-dd", from));
			sb.append("|");
			sb.append(DateFormat.format("YYYY-mm-dd", to));
		}
		params.put(key, sb.toString());
	}

	@Override
	public versions getMinVersion(){
		return versions.v110;
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("issues."+getExtention());
		url.appendEncodedPath(getExtention());
		for(Entry<String,String> data : params.entrySet()){
			if(data.getValue() != null){
				url.appendQueryParameter(data.getKey(), data.getValue());
			}
		}
		return url;
	}
}
