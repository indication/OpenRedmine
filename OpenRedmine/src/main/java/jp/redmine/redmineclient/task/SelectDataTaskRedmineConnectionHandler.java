package jp.redmine.redmineclient.task;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.url.RemoteUrl;

public class SelectDataTaskRedmineConnectionHandler extends SelectDataTaskConnectionHandler {
	private RedmineConnection connection;
	public SelectDataTaskRedmineConnectionHandler(RedmineConnection con){
		connection = con;
	}

	public static String getUrl(RedmineConnection connection, RemoteUrl url){
		url.setupRequest(RemoteUrl.requests.xml);
		return url.getUrl(connection.getUrl()).build().toString();
	}

	public String getUrl(RemoteUrl url){
		return getUrl(connection, url);
	}

	@Override
	public void setupOnMessage(HttpURLConnection msg){
		String key = connection.getToken();
		if(!TextUtils.isEmpty(key))
			msg.setRequestProperty("X-Redmine-API-Key", key);
		if(connection.isAuth()) {
			String encoded = connection.getAuthId() + ":" + connection.getAuthPasswd();
			try {
				encoded = Base64.encodeToString(encoded.getBytes("UTF-8"), Base64.NO_WRAP);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			msg.setRequestProperty("Authorization", "Basic " + encoded);
		}

	}

}
