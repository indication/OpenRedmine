package jp.redmine.redmineclient.url;

import android.net.Uri;

public class RemoteUrlTrackers extends RemoteUrl {

	@Override
	public versions getMinVersion(){
		return versions.v130;
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("trackers." + getExtention());
		return url;
	}
}
