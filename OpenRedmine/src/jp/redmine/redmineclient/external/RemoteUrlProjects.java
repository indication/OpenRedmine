package jp.redmine.redmineclient.external;

import android.net.Uri;

public class RemoteUrlProjects extends RemoteUrl {

	@Override
	public versions getMinVersion(){
		return versions.v110;
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("projects." + getExtention());
		return url;
	}
}
