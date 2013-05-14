package jp.redmine.redmineclient.task;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.external.lib.AuthenticationParam;
import jp.redmine.redmineclient.external.lib.ClientParam;
import jp.redmine.redmineclient.external.lib.ConnectionHelper;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;

import android.net.Uri;
import android.text.TextUtils;

class SelectDataTaskRedmineConnectionHandler extends SelectDataTaskConnectionHandler {
	private RedmineConnection connection;
	public SelectDataTaskRedmineConnectionHandler(RedmineConnection con){
		connection = con;
	}

	protected static DefaultHttpClient getHttpClient(RedmineConnection connection){
		ClientParam clientparam = new ClientParam();
		clientparam.setSLLTrustAll(connection.isPermitUnsafe());
		clientparam.setCertKey(connection.getCertKey());
		clientparam.setTimeout(120000);
		DefaultHttpClient client = ConnectionHelper.createHttpClient(clientparam);
		if(connection.isAuth()){
			Uri remoteurl = Uri.parse(connection.getUrl());
			AuthenticationParam param = new AuthenticationParam();
			param.setId(connection.getAuthId());
			param.setPass(connection.getAuthPasswd());
			param.setAddress(remoteurl.getHost());
			if(remoteurl.getPort() <= 0){
				param.setPort("https".equals(remoteurl.getScheme()) ? 443: 80);
			} else {
				param.setPort(remoteurl.getPort());
			}
			ConnectionHelper.setupHttpClientAuthentication(client, param);
		}
		return client;
	}

	@Override
	protected DefaultHttpClient getHttpClientCore() {
		return getHttpClient(connection);
	}

	@Override
	public void setupOnMessage(AbstractHttpMessage msg){
		String key = connection.getToken();
		if(TextUtils.isEmpty(key))
			return;
		msg.setHeader("X-Redmine-API-Key", key);
	}

}
