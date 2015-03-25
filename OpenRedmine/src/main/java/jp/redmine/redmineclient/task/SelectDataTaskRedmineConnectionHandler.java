package jp.redmine.redmineclient.task;

import android.net.Uri;
import android.text.TextUtils;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ClientConnectionManager;
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

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.url.RemoteUrl;

public class SelectDataTaskRedmineConnectionHandler extends SelectDataTaskConnectionHandler {
	private RedmineConnection connection;
	private ClientConnectionManager manager;
	private HttpParams httpparams = new BasicHttpParams();
	public SelectDataTaskRedmineConnectionHandler(RedmineConnection con){
		HttpConnectionParams.setConnectionTimeout(httpparams, 120000);
		HttpConnectionParams.setSoTimeout(httpparams, 120000);
		connection = con;
		manager = new ThreadSafeClientConnManager(httpparams
				, getSchemeRegistry(connection.isPermitUnsafe(),connection.getCertKey()));
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

	protected DefaultHttpClient getHttpClient(RedmineConnection connection){
		DefaultHttpClient client = new DefaultHttpClient(manager, httpparams);
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

	public static String getUrl(RedmineConnection connection, RemoteUrl url){
		url.setupRequest(RemoteUrl.requests.xml);
		return url.getUrl(connection.getUrl()).build().toString();
	}

	public String getUrl(RemoteUrl url){
		return getUrl(connection, url);
	}

	@Override
	protected DefaultHttpClient getHttpClientCore() {
		return getHttpClient(connection);
	}

	@Override
	public void close() {
		manager.closeExpiredConnections();
		super.close();
	}

	@Override
	public void setupOnMessage(AbstractHttpMessage msg){
		String key = connection.getToken();
		if(TextUtils.isEmpty(key))
			return;
		msg.setHeader("X-Redmine-API-Key", key);
	}

	public RedmineConnection getConnection(){
		return connection;
	}
}
