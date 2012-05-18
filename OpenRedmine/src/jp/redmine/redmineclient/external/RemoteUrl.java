package jp.redmine.redmineclient.external;

import android.net.Uri;

public abstract class RemoteUrl {
	protected Uri.Builder url;
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

	public RemoteUrl(String url,versions version,requests request){
		Uri data = Uri.parse(url);
		this.url =  data.buildUpon();
		this.version = version;
		this.request = request;
	}

	public boolean IsSupported(){
		if( getMinVersion().ordinal() <= version.ordinal()
			&& getMaxVersion().ordinal() >= version.ordinal()){
			return true;
		} else {
			return false;
		}
	}

	abstract public Uri.Builder getUrl();
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
