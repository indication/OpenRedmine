package jp.redmine.redmineclient.external;

import android.net.Uri;

public abstract class RemoteUrl {
	protected versions version;
	protected requests request;
	public enum versions {
		min
		,v110
		,v130
		,max
	}
	public enum requests {
		json
		,xml
	}

	public void setupRequest(requests request){
		this.request = request;
	}

	public void setupVersion(versions version){
		this.version = version;
	}



	public boolean IsSupported(){
		if( getMinVersion().ordinal() <= version.ordinal()
			&& getMaxVersion().ordinal() >= version.ordinal()){
			return true;
		} else {
			return false;
		}
	}

	protected Uri.Builder convertUrl(String url){
		Uri data = Uri.parse(url);
		return data.buildUpon();
	}

	abstract public Uri.Builder getUrl(String baseurl);
	public versions getMinVersion(){
		return versions.min;
	}
	public versions getMaxVersion(){
		return versions.max;
	}

	protected final String getExtention(){
		return request.toString();
	}




}
