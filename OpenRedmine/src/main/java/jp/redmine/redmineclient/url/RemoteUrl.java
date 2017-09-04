package jp.redmine.redmineclient.url;

import android.net.Uri;

public abstract class RemoteUrl {
	protected requests request;
	public enum requests {
		json
		,xml
	}

	public void setupRequest(requests request){
		this.request = request;
	}

	protected Uri.Builder convertUrl(String url){
		Uri data = Uri.parse(url);
		return data.buildUpon();
	}

	abstract public Uri.Builder getUrl(String baseurl);

	protected final String getExtension(){
		return request.toString();
	}




}
