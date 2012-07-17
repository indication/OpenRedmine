package jp.redmine.redmineclient.external.lib;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.transdroid.util.FakeSocketFactory;

public class ConnectionHelper {

	public static void setupHttpGet(HttpGet client){

	}
	public static void setupHttpClientAuthentication(DefaultHttpClient client, AuthenticationParam auth){
		client.getCredentialsProvider().setCredentials(
			new AuthScope(auth.getAddress(), auth.getPort()),
			new UsernamePasswordCredentials(auth.getId(), auth.getPass()));
	}

	public static DefaultHttpClient createHttpClient(ClientParam settings) {

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", new PlainSocketFactory(), settings.getHttpPort()));
		SocketFactory https_socket =
			  settings.isSLLTrustAll() 				? new FakeSocketFactory()
			: settings.getCertKey() != null			? new FakeSocketFactory(settings.getCertKey())
			: SSLSocketFactory.getSocketFactory();
		registry.register(new Scheme("https", https_socket, settings.getHttpsPort()));

		HttpParams httpparams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpparams, settings.getTimeout());
		HttpConnectionParams.setSoTimeout(httpparams, settings.getTimeout());
		DefaultHttpClient httpclient = new DefaultHttpClient(
				new ThreadSafeClientConnManager(httpparams, registry)
				, httpparams);

		return httpclient;
	}


}
