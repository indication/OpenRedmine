package jp.redmine.redmineclient.task;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

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
	static HttpUriRequest getHttpUriRequest(RemoteType type,
			SelectDataTaskPutHandler puthandler, URI uri)
			throws SQLException, TransformerException, ParserConfigurationException, IOException {

		switch(type){
			case get:
				HttpGet get = new HttpGet(uri);
				return get;
			case delete:
				HttpDelete del = new HttpDelete();
				return del;
			case post:
				HttpPost post = new HttpPost(uri);
				post.setEntity(puthandler.getContent());
				return post;
			case put:
				HttpPut put = new HttpPut(uri);
				put.setEntity(puthandler.getContent());
				return put;
			default:
				break;
		}
		return null;
	}
	static boolean isGZipHttpResponse(HttpResponse response) {
		Header header = response.getEntity().getContentEncoding();
		if (header == null) return false;
		String value = header.getValue();
		return (!TextUtils.isEmpty(value) && value.contains("gzip"));
	}
	static boolean isDeflateHttpResponse(HttpResponse response) {
		Header header = response.getEntity().getContentEncoding();
		if (header == null) return false;
		String value = header.getValue();
		return (!TextUtils.isEmpty(value) && value.contains("deflate"));
	}
	static class DebugResponseHandler implements ResponseHandler<Boolean> {
		@Override
		public Boolean handleResponse(HttpResponse response) throws IOException {
			int status = response.getStatusLine().getStatusCode();
			InputStream stream = response.getEntity().getContent();
			if (isGZipHttpResponse(response)) {
				stream =  new GZIPInputStream(stream);
			} else if(isDeflateHttpResponse(response)){
				stream =  new InflaterInputStream(stream);
			}
			Log.d("requestDebug", "Status: " + String.valueOf(status));
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String str;
			Log.d("requestDebug", ">>Dump start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			while((str = reader.readLine()) != null){
				Log.d("requestDebug", str);
			}
			Log.d("requestDebug", "<<Dump end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			return false;
		}
	}
	static class ContentResponseHandler implements ResponseHandler<Boolean> {
		SelectDataTaskDataHandler handler;
		ContentResponseErrorHandler handlerError;
		public ContentResponseHandler(SelectDataTaskDataHandler h, ContentResponseErrorHandler ev){
			handler = h;
			handlerError = ev;
		}
		@Override
		public Boolean handleResponse(HttpResponse response) throws IOException {
			int status = response.getStatusLine().getStatusCode();
			long length = response.getEntity().getContentLength();
			if(BuildConfig.DEBUG){
				Log.i("request", "Status: " + status);
				Log.i("request", "Protocol: " + response.getProtocolVersion());
				Log.i("request", "Length: " + length);
			}
			InputStream stream = response.getEntity().getContent();
			if (isGZipHttpResponse(response)) {
				if(BuildConfig.DEBUG) Log.i("request", "Gzip: Enabled");
				stream =  new GZIPInputStream(stream);
			} else if(isDeflateHttpResponse(response)){
				if(BuildConfig.DEBUG) Log.i("request", "Deflate: Enabled");
				stream =  new InflaterInputStream(stream);
			}
			switch(status){
				case HttpStatus.SC_OK:
				case HttpStatus.SC_CREATED:
					try {
						if(length != 0)
							handler.onContent(stream);
						return true;
					} catch (XmlPullParserException e) {
						handlerError.onError(e);
					} catch (SQLException e) {
						handlerError.onError(e);
					}
					break;
				default:
					handlerError.onErrorRequest(status);
					if(BuildConfig.DEBUG){
						Log.d("requestError", "Status: " + String.valueOf(status));
						BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
						String str;
						while((str = reader.readLine()) != null){
							Log.d("requestError", str);
						}
					}
					break;
			}
			return false;
		}
	}
	interface ContentResponseErrorHandler {
		void onErrorRequest(int status);
		void onError(Exception e);
	}

	static boolean fetchData(RemoteType type
			,SelectDataTaskConnectionHandler connectionhandler
			,String url
			,SelectDataTaskDataHandler handler
			,SelectDataTaskPutHandler puthandler
			,ContentResponseErrorHandler errorhandler
	){
		DefaultHttpClient client = connectionhandler.getHttpClient();
		Boolean isOk = false;
		try {
			URI uri = new URI(url);
			HttpUriRequest msg = Fetcher.getHttpUriRequest(type, puthandler, uri);
			if (msg == null)
				return false;
			connectionhandler.setupOnMessage(msg);
			msg.setHeader("Accept-Encoding", "gzip, deflate");
			if(BuildConfig.DEBUG){
				Log.i("request", "Url: " + msg.getURI().toASCIIString());
				for(Header h : msg.getAllHeaders())
					Log.d("request", "Header:" + h.toString());
				if(type == RemoteType.get && BuildConfig.DEBUG_XML){
					client.execute(msg, new Fetcher.DebugResponseHandler());
				}
			}
			// fetch remote
			isOk = client.execute(msg, new Fetcher.ContentResponseHandler(handler, errorhandler));
		} catch (URISyntaxException e) {
			errorhandler.onErrorRequest(404);
		} catch (ClientProtocolException e) {
			errorhandler.onError(e);
		} catch (IOException e) {
			errorhandler.onError(e);
		} catch (SQLException e) {
			errorhandler.onError(e);
		} catch (IllegalArgumentException e) {
			errorhandler.onError(e);
		} catch (ParserConfigurationException e) {
			errorhandler.onError(e);
		} catch (TransformerException e) {
			errorhandler.onError(e);
		}
		if(!isOk)
			connectionhandler.close();
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
