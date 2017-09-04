package jp.redmine.redmineclient.url;

import android.net.Uri;

public class RemoteUrlEnumerations extends RemoteUrl {

	public enum EnumerationType{
		IssuePriorities
		,TimeEntryActivities
	}
	private EnumerationType type;
	/**
	 * @return type
	 */
	public EnumerationType getType() {
		return type;
	}
	/**
	 * @param type セットする type
	 */
	public void setType(EnumerationType type) {
		this.type = type;
	}
	protected String getTypeString(){
		switch(type){
		case IssuePriorities:
			return "issue_priorities";
		case TimeEntryActivities:
			return "time_entry_activities";
		default:
			return "";
		}
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("enumerations");
		url.appendEncodedPath(getTypeString() + "." + getExtension());
		return url;
	}
}
