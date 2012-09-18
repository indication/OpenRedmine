package jp.redmine.redmineclient.url;

import java.util.HashMap;
import java.util.Map.Entry;

import android.net.Uri;

public class RemoteUrlIssue extends RemoteUrl {
	private HashMap<String,String> params = new HashMap<String,String>();

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
