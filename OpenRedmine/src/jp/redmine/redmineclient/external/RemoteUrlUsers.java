package jp.redmine.redmineclient.external;

import android.net.Uri;

public class RemoteUrlUsers extends RemoteUrl {

	@Override
	public versions getMinVersion(){
		return versions.v130;
	}
	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("users." + getExtention());
		return url;
	}
}
