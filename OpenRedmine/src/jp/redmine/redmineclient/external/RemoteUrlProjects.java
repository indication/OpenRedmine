package jp.redmine.redmineclient.external;

import android.net.Uri.Builder;

public class RemoteUrlProjects extends RemoteUrl {

	public RemoteUrlProjects(String url, versions version, requests request) {
		super(url, version,request);
	}
	@Override
	public versions getMinVersion(){
		return versions.v110;
	}
	@Override
	public Builder getUrl() {
		url.appendEncodedPath("/projects." + getExtention());
		return url;
	}
}
