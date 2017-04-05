package jp.redmine.redmineclient.url;

import android.net.Uri;

import jp.redmine.redmineclient.entity.RedmineProject;

public class RemoteUrlNews extends RemoteUrl {
	private String project;

	public void setProject(RedmineProject proj){
		if(proj.getIdentifier() != null)
			project = proj.getIdentifier();
		else if(proj.getProjectId() != null)
			project = String.valueOf(proj.getProjectId());
	}
	public void setProject(String proj){
		project = proj;
	}

	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("projects/"+project+"/news."+ getExtension());
		return url;
	}
}
