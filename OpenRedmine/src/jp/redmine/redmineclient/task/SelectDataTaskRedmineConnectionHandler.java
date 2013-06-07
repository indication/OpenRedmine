package jp.redmine.redmineclient.task;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.external.lib.AuthenticationParam;
import jp.redmine.redmineclient.external.lib.ClientParam;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.transdroid.daemon.util.FakeSocketFactory;

import android.net.Uri;
import android.text.TextUtils;

class SelectDataTaskRedmineConnectionHandler extends SelectDataTaskConnectionHandler {
	private RedmineConnection connection;
	public SelectDataTaskRedmineConnectionHandler(RedmineConnection con){
		connection = con;
	}

	public static SchemeRegistry getSchemeRegistry(boolean isTrustAll, String certkey) {
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", new PlainSocketFactory(), 80));
		SocketFactory https_socket =
				isTrustAll					? new FakeSocketFactory()
			: !TextUtils.isEmpty(certkey)	? new FakeSocketFactory(certkey)
			: SSLSocketFactory.getSocketFactory();
		registry.register(new Scheme("https", https_socket, 443));
		return registry;

	}
	public static DefaultHttpClient createHttpClient(ClientParam settings) {
		HttpParams httpparams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpparams, settings.getTimeout());
		HttpConnectionParams.setSoTimeout(httpparams, settings.getTimeout());
		DefaultHttpClient httpclient = new DefaultHttpClient(
				new ThreadSafeClientConnManager(httpparams
						, getSchemeRegistry(settings.isSLLTrustAll(),settings.getCertKey()))
				, httpparams);

		return httpclient;
	}
	public static void setupHttpClientAuthentication(DefaultHttpClient client, AuthenticationParam auth){
		client.getCredentialsProvider().setCredentials(
			new AuthScope(auth.getAddress(), auth.getPort()),
			new UsernamePasswordCredentials(auth.getId(), auth.getPass()));
	}

	protected static DefaultHttpClient getHttpClient(RedmineConnection connection){
		ClientParam clientparam = new ClientParam();
		clientparam.setSLLTrustAll(connection.isPermitUnsafe());
		clientparam.setCertKey(connection.getCertKey());
		clientparam.setTimeout(120000);
		DefaultHttpClient client = createHttpClient(clientparam);
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
			setupHttpClientAuthentication(client, param);
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
