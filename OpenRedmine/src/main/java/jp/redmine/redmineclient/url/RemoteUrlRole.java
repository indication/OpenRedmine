package jp.redmine.redmineclient.url;

import android.net.Uri;

public class RemoteUrlRole extends RemoteUrl {

	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("roles." + getExtension());
		return url;
	}
}
