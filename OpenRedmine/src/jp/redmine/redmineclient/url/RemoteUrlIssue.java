package jp.redmine.redmineclient.url;

import java.util.HashMap;
import java.util.Map.Entry;

import android.net.Uri;
import android.text.TextUtils;

public class RemoteUrlIssue extends RemoteUrl {
	private HashMap<String,String> params = new HashMap<String,String>();
	private Integer issue_id;

	public enum IssueOption{
		None,
		WithChangesets,
		WithJournals,
		WithChangesetsJournals,
	}

	public void setOption(IssueOption opt){
		switch(opt){
		case None:
			if(params.containsKey("include")){
				params.remove("include");
			}
			break;
		case WithJournals:
			params.put("include","journals");
			break;
		case WithChangesets:
			params.put("include","changesets");
			break;
		case WithChangesetsJournals:
			params.put("include","journals,changesets");
			break;
		}
	}

	public void setIssueId(Integer id){
		issue_id = id;
	}
	public void setIssueId(String id){
		if(TextUtils.isEmpty(id))
			setIssueId((Integer)null);
		if(id.matches("^-?\\d+$")){
			setIssueId(Integer.parseInt(id));
		}
	}
	@Override
	public versions getMinVersion(){
		return versions.v110;
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		if(issue_id == null){
			url.appendEncodedPath("issues."+getExtention());
		} else {
			url.appendEncodedPath("issue/" + String.valueOf(issue_id) + "."+getExtention());
		}
		url.appendEncodedPath(getExtention());
		for(Entry<String,String> data : params.entrySet()){
			if(data.getValue() != null){
				url.appendQueryParameter(data.getKey(), data.getValue());
			}
		}
		return url;
	}
}
