package jp.redmine.redmineclient.external;

import android.net.Uri;

public class RemoteUrlProject extends RemoteUrl {
	private String ProjectID;
	private String Includes;
	public static class IncludeTypes {
		public static String Trackers = "trackers";
		public static String IssueCategories = "issue_categories";

	}

	public void setProjectID(String id){
		ProjectID = id;
	}
	public void setProjectID(int id){
		ProjectID = Integer.toString(id);
	}

	public void setIncludes(String include){
		//@todo includetype checks
		Includes = include;
	}

	@Override
	public versions getMinVersion(){
		return versions.v110;
	}

	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("/projects/"+ProjectID+"."+getExtention());
		if(!"".equals(Includes)) {
			url.appendQueryParameter("include", Includes);
		}
		return url;
	}
}
