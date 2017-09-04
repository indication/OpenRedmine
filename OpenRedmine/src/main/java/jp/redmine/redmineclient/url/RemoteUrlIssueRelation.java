package jp.redmine.redmineclient.url;

import android.net.Uri;
import android.text.TextUtils;

import jp.redmine.redmineclient.entity.TypeConverter;

public class RemoteUrlIssueRelation extends RemoteUrl {
	private Integer issue_id;
	private Integer relations_id;

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
	public void setRelationId(Integer id){
		relations_id = id;
	}
	public void setRelationId(String id){
		if(TextUtils.isEmpty(id))
			setRelationId((Integer)null);
		if(id.matches("^-?\\d+$")){
			setRelationId(TypeConverter.parseInteger(id));
		}
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		if(issue_id != null){
			url.appendEncodedPath("issues");
			url.appendEncodedPath(String.valueOf(issue_id));
			url.appendEncodedPath("relations." + getExtension());
		} else 	if(relations_id == null){
			url.appendEncodedPath("relations." + getExtension());
		} else {
			url.appendEncodedPath("relations");
			url.appendEncodedPath(String.valueOf(relations_id) + "." + getExtension());
		}
		return url;
	}
}
