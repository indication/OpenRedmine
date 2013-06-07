package jp.redmine.redmineclient.task;

import jp.redmine.redmineclient.entity.RedmineConnection;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
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

	protected static DefaultHttpClient getHttpClient(RedmineConnection connection){
		HttpParams httpparams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpparams, 120000);
		HttpConnectionParams.setSoTimeout(httpparams, 120000);
		DefaultHttpClient client = new DefaultHttpClient(
				new ThreadSafeClientConnManager(httpparams
						, getSchemeRegistry(connection.isPermitUnsafe(),connection.getCertKey()))
				, httpparams);
		if(connection.isAuth()){
			Uri remoteurl = Uri.parse(connection.getUrl());
			Credentials credential = new UsernamePasswordCredentials(connection.getAuthId(), connection.getAuthPasswd());
			int port = remoteurl.getPort();
			if(port <= 0){
				port = "https".equals(remoteurl.getScheme()) ? 443: 80;
			}
			AuthScope scope = new AuthScope(remoteurl.getHost(),port);
			client.getCredentialsProvider().setCredentials(scope, credential);
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
