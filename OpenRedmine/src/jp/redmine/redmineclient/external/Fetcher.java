package jp.redmine.redmineclient.external;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.external.lib.FakeSocketFactory;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.net.Uri;
import android.util.Xml;

public class Fetcher {
	private RemoteUrl remoteurl;
	private XmlPullParser xmlPullParser = Xml.newPullParser();
	private int status = 0;
	private HttpAuthenticator authinfo = null;
	private BaseParser<?> parser;
	private boolean isIgnoreSSLVerification = false;

	public void setAuthentication(String id, String pass){
		authinfo = new HttpAuthenticator(id, pass);
		Authenticator.setDefault(authinfo);
	}
	public void unsetAuthentication(){
		Authenticator.setDefault(null);
	}
	public void setIgnoreSSLVerification(boolean verify){
		isIgnoreSSLVerification = verify;
	}

	public boolean fetchData(RedmineConnection connectinfo) throws Throwable{

		Connection con = new Connection(connectinfo);
		//con.requestGet()
		con.setupRemoteUrl(remoteurl);
		xmlPullParser.setInput(con.requestGet(), "UTF-8");
		return true;
	}

	public boolean Parse() throws XmlPullParserException, IOException{
		if(!isStatusSuccess(status))
			return false;
		parser.setXml(xmlPullParser);
		parser.parse();
		return true;
	}


	public String fetchError() throws XmlPullParserException, IOException {
		int state;
		StringBuilder sb = new StringBuilder();

		while((state = xmlPullParser.next())== XmlPullParser.END_DOCUMENT)
		{
			if(state==XmlPullParser.START_TAG
					&& xmlPullParser.getName().toLowerCase() == "error"){
				sb.append(xmlPullParser.getText());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	protected boolean isStatusSuccess(int status){
		// return true with 2xx
		return ((int)(status / 100) == 2);
	}




	/**
	 * @param remoteurl セットする remoteurl
	 */
	public void setRemoteurl(RemoteUrl remoteurl) {
		this.remoteurl = remoteurl;
	}

	/**
	 * @return remoteurl
	 */
	public RemoteUrl getRemoteurl() {
		return remoteurl;
	}



	/**
	 * @param parser セットする parser
	 */
	public void setParser(BaseParser<?> parser) {
		this.parser = parser;
	}
	/**
	 * @return parser
	 */
	public BaseParser<?> getParser() {
		return parser;
	}



	private class HttpAuthenticator extends Authenticator {
		private String username;
		private String password;
		public HttpAuthenticator(final String username, final String password){
			super();
			this.username = username;
			this.password = password;
		}
		protected PasswordAuthentication getPasswordAuthentication(){
			return new
			PasswordAuthentication(username, password.toCharArray());
		}
	}

	private static class FakeHostnameVerifier implements javax.net.ssl.HostnameVerifier {
		public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
			return true;
		}
	}
}
