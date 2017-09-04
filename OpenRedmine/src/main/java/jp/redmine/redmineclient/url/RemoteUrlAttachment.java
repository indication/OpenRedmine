package jp.redmine.redmineclient.url;

import android.net.Uri;

public class RemoteUrlAttachment extends RemoteUrl {
	private String attachment;

	public void setAttachment(String attachmentid){
		attachment = attachmentid;
	}

	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("attachments/"+attachment+"." + getExtension());
		return url;
	}
}
