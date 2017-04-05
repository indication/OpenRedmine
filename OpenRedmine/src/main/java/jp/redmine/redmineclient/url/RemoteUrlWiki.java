package jp.redmine.redmineclient.url;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import jp.redmine.redmineclient.entity.RedmineProject;

public class RemoteUrlWiki extends RemoteUrl {
	private HashMap<String,String> params = new HashMap<String,String>();
	public static final String list = "index";
	private String project;
	private String title;

	public enum Includes{
		Attachments("attachments"),
		;
		private String name;
		Includes(String nm){
			name = nm;
		}
		public String getName(){
			return name;
		}
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void filterLimit(int limit){
		params.put("limit", Integer.toString(limit));
	}
	public void filterOffset(int offset){
		params.put("offset", Integer.toString(offset));
	}
	public void setProject(RedmineProject proj){
		if(proj.getIdentifier() != null)
			project = proj.getIdentifier();
		else if(proj.getProjectId() != null)
			project = String.valueOf(proj.getProjectId());
	}
	public void setProject(String proj){
		project = proj;
	}

	public void setInclude(Includes ... args){
		StringBuilder sb = new StringBuilder();
		for(Includes inc : args){
			if(sb.length()>1){
				sb.append(",");
			}
			sb.append(inc.getName());
		}
		params.put("include", sb.length()>1 ? sb.toString() : null);
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("projects/"+project+"/wiki/"+title+"." + getExtension());

		for(Map.Entry<String,String> data : params.entrySet()){
			if(data.getValue() != null){
				url.appendQueryParameter(data.getKey(), data.getValue());
			}
		}
		return url;
	}
}
