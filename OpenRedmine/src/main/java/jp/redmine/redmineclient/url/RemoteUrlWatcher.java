package jp.redmine.redmineclient.url;

import android.net.Uri;
import android.text.TextUtils;

import jp.redmine.redmineclient.entity.TypeConverter;

public class RemoteUrlWatcher extends RemoteUrl {
	private Integer issue_id;
	private Integer watcher_id;

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
	public void setWatcherId(Integer id){
		watcher_id = id;
	}
	public void setWatcherId(String id){
		if(TextUtils.isEmpty(id))
			setWatcherId((Integer)null);
		if(id.matches("^-?\\d+$")){
			setWatcherId(TypeConverter.parseInteger(id));
		}
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		if(issue_id != null){
			url.appendEncodedPath("issues");
			url.appendEncodedPath(String.valueOf(issue_id));
		}
		if(watcher_id == null){
			url.appendEncodedPath("watchers." + getExtension());
		} else {
			url.appendEncodedPath("watchers");
			url.appendEncodedPath(String.valueOf(watcher_id) + "." + getExtension());
		}
		return url;
	}
}
