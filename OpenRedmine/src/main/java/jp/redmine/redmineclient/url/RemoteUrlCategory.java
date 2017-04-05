package jp.redmine.redmineclient.url;

import jp.redmine.redmineclient.entity.RedmineProject;
import android.net.Uri;

public class RemoteUrlCategory extends RemoteUrl {
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
		url.appendEncodedPath("projects/"+project+"/issue_categories."+ getExtension());
		return url;
	}
}
