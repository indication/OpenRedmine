package jp.redmine.redmineclient.url;

import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map.Entry;

import jp.redmine.redmineclient.entity.TypeConverter;

public class RemoteUrlIssue extends RemoteUrl {
	private HashMap<String,String> params = new HashMap<String,String>();
	private Integer issue_id;

	public enum Includes{
		Relations("relations"),
		Attachments("attachments"),
		Changesets("changesets"),
		Journals("journals"),
		Watchers("watchers"),
		;
		private String name;
		Includes(String nm){
			name = nm;
		}
		public String getName(){
			return name;
		}
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

	public void setIssueId(Integer id){
		issue_id = id;
	}
	public void setIssueId(String id){
		if(TextUtils.isEmpty(id))
			setIssueId((Integer)null);
		if(id.matches("^-?\\d+$")){
			setIssueId(TypeConverter.parseInteger(id));
		}
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		if(issue_id == null){
			url.appendEncodedPath("issues."+ getExtension());
		} else {
			url.appendEncodedPath("issues/" + String.valueOf(issue_id) + "."+ getExtension());
		}
		for(Entry<String,String> data : params.entrySet()){
			if(data.getValue() != null){
				url.appendQueryParameter(data.getKey(), data.getValue());
			}
		}
		return url;
	}
}
