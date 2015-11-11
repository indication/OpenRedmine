package jp.redmine.redmineclient.task;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.parser.BaseParser;


public class Fetcher {
	static public final String CHARSET = "UTF-8";
	protected enum RemoteType{
		get,
		put,
		post,
		delete,
	}
	public interface ContentResponseErrorHandler {
		void onErrorRequest(int status);
		void onError(Exception e);
	}

	static boolean fetchData(RemoteType type
			,SelectDataTaskConnectionHandler connectionhandler
			,String url
			,SelectDataTaskDataHandler handler
			,SelectDataTaskPutHandler puthandler
			,ContentResponseErrorHandler errorhandler
	) {

		Boolean isOk = false;
		HttpURLConnection con = null;
		try {
			URL remoteUrl = new URL(url);
			switch (remoteUrl.getProtocol()) {
				case "http":
				case "ftp":
					con = (HttpURLConnection) remoteUrl.openConnection();
					break;
				case "https":
					HttpsURLConnection cons = (HttpsURLConnection) remoteUrl.openConnection();

					TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {

						private String certKey = null;
						@Override
						public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
							if( this.certKey == null ){
								// This is the Accept All certificates case.
								return;
							}

							// Otherwise, we have a certKey defined. We should now examine the one we got from the server.
							// They match? All is good. They don't, throw an exception.
							String our_key = this.certKey.replaceAll("[^a-f0-9]+", "");
							try {
								//Assume self-signed root is okay?
								X509Certificate ss_cert = x509Certificates[0];
								String thumbprint = getThumbPrint(ss_cert);
								Log.d("", thumbprint);
								if( !our_key.equalsIgnoreCase(thumbprint) ){
									throw new CertificateException("Certificate key [" + thumbprint + "] doesn't match expected value.");
								}
							} catch (NoSuchAlgorithmException e) {
								throw new CertificateException("Unable to check self-signed cert, unknown algorithm. " + e.toString());
							}

						}

						// Thank you: http://stackoverflow.com/questions/1270703/how-to-retrieve-compute-an-x509-certificates-thumbprint-in-java
						private String getThumbPrint(X509Certificate cert) throws NoSuchAlgorithmException, CertificateEncodingException {
							MessageDigest md = MessageDigest.getInstance("SHA-1");
							byte[] der = cert.getEncoded();
							md.update(der);
							byte[] digest = md.digest();
							return hexify(digest);
						}

						private String hexify (byte bytes[]) {
							char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7',
									'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

							StringBuffer buf = new StringBuffer(bytes.length * 2);

							for (int i = 0; i < bytes.length; ++i) {
								buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
								buf.append(hexDigits[bytes[i] & 0x0f]);
							}

							return buf.toString();
						}
						@Override
						public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

						}

						@Override
						public X509Certificate[] getAcceptedIssuers() {
							return new X509Certificate[]{};
						}
					}};
					try {
						SSLContext context = SSLContext.getInstance("TLS");
						context.init(null, trustManagers, new SecureRandom());
						cons.setSSLSocketFactory(context.getSocketFactory());
					} catch (NoSuchAlgorithmException | KeyManagementException e) {
						errorhandler.onError(e);
					}
					con = cons;
					break;
				default:
					return false;
			}
			connectionhandler.setupOnMessage(con);
			con.setRequestProperty("Accept-Encoding", "gzip, deflate");
			switch(type){
				case get:
					con.setRequestMethod("GET");
					break;
				case delete:
					con.setRequestMethod("DELETE");
					break;
				case post:
					con.setRequestMethod("POST");
					con.setDoOutput(true);
					puthandler.getContent(con);
					break;
				case put:
					con.setRequestMethod("PUT");
					con.setDoOutput(true);
					puthandler.getContent(con);
					break;
				default:
					return false;
			}
			con.connect();
			int response = con.getResponseCode();
			switch (response) {
				case HttpURLConnection.HTTP_ACCEPTED:
				case HttpURLConnection.HTTP_OK:
					InputStream stream = con.getInputStream();
					String encoding = con.getContentEncoding();
					if(encoding == null) {
					} else if (encoding.contains("gzip")){
						if(BuildConfig.DEBUG) Log.i("request", "GZip: Enabled");
						stream =  new GZIPInputStream(stream);
					} else if (encoding.contains("deflate")){
						if(BuildConfig.DEBUG) Log.i("request", "Deflate: Enabled");
						stream =  new InflaterInputStream(stream);
					}
					handler.onContent(stream);
					isOk = true;
					break;
				default:
					errorhandler.onErrorRequest(response);
					if(BuildConfig.DEBUG){
						Log.d("requestError", "Status: " + String.valueOf(response));
						BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
						String str;
						while((str = reader.readLine()) != null){
							Log.d("requestError", str);
						}
					}
					isOk = false;
					break;
			}

		} catch (XmlPullParserException | ParserConfigurationException | TransformerException | SQLException | IOException e) {
			errorhandler.onError(e);
		} finally {
			if(con != null)
				con.disconnect();
		}
		return isOk;
	}

	static public boolean fetchData(SelectDataTaskConnectionHandler connectionhandler,ContentResponseErrorHandler errorhandler,
							 String url,SelectDataTaskDataHandler handler){
		return fetchData(RemoteType.get,connectionhandler,url,handler,null, errorhandler);
	}
	static public boolean putData(SelectDataTaskConnectionHandler connectionhandler,ContentResponseErrorHandler errorhandler,
						   String url,SelectDataTaskDataHandler handler, SelectDataTaskPutHandler puthandler){
		return fetchData(RemoteType.put,connectionhandler,url,handler,puthandler, errorhandler);
	}
	static public boolean postData(SelectDataTaskConnectionHandler connectionhandler,ContentResponseErrorHandler errorhandler,
							String url,SelectDataTaskDataHandler handler, SelectDataTaskPutHandler puthandler){
		return fetchData(RemoteType.post,connectionhandler,url,handler,puthandler, errorhandler);
	}


	public static void setupParserStream(InputStream stream,BaseParser<?,?> parser) throws XmlPullParserException{
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xmlPullParser = factory.newPullParser();
		xmlPullParser.setInput(stream, CHARSET);
		parser.setXml(xmlPullParser);
	}
}
