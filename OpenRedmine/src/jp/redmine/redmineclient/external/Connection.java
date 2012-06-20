package jp.redmine.redmineclient.external;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;

import com.Ostermiller.util.CircularByteBuffer;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.external.lib.AuthenticationParam;
import jp.redmine.redmineclient.external.lib.ClientParam;
import jp.redmine.redmineclient.external.lib.ConnectionHelper;
import jp.redmine.redmineclient.url.RemoteUrl;
import jp.redmine.redmineclient.url.RemoteUrl.requests;
import jp.redmine.redmineclient.url.RemoteUrl.versions;

public class Connection {
	private RedmineConnection connection;
	private Uri remoteurl;
	protected int status;

	public Connection(RedmineConnection con){
		this.connection = con;
	}

	public void setupRemoteUrl(RemoteUrl remote){
		remote.setupRequest(requests.xml);
		remote.setupVersion(versions.v130);
		Builder builder = remote.getUrl(connection.getUrl());
		remoteurl = builder.build();
	}


	protected DefaultHttpClient getHttpClient(){
		DefaultHttpClient client = ConnectionHelper.createHttpClient(getClientParam(remoteurl));
		if(connection.isAuth()){
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

	protected ClientParam getClientParam(Uri remoteurl){
		ClientParam clientparam = new ClientParam();
		if(remoteurl.getPort() <= 0){
			clientparam.setHttpPort(80);
			clientparam.setHttpsPort(443);
		} else if("https".equals(remoteurl.getScheme())){
			clientparam.setHttpPort(80);
			clientparam.setHttpsPort(remoteurl.getPort());
		} else {
			clientparam.setHttpPort(remoteurl.getPort());
			clientparam.setHttpsPort(443);
		}
		clientparam.setSLLTrustAll(true);
		clientparam.setTimeout(30000);
		return clientparam;
	}

	private void setApiKey(AbstractHttpMessage msg){
		if("".equals(connection.getToken())){
			return;
		}
		msg.setHeader("X-Redmine-API-Key", connection.getToken());
	}

	protected HttpGet getHttpGet() throws URISyntaxException{
		HttpGet get = new HttpGet(new URI(remoteurl.toString()));
		setApiKey(get);
		return get;
	}


	protected HttpPut getHttpPut() throws URISyntaxException{
		HttpPut put = new HttpPut(new URI(remoteurl.toString()));
		setApiKey(put);
		return put;
	}

	protected HttpDelete getHttpDelete() throws URISyntaxException{
		HttpDelete put = new HttpDelete(new URI(remoteurl.toString()));
		setApiKey(put);
		return put;
	}


	public InputStream requestGet(){
		final CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
		Thread t = new Thread(new Runnable() {
			public void run() {
				DefaultHttpClient client = getHttpClient();
				HttpGet get;
				HttpResponse response;
				try {
					get = getHttpGet();
					response = client.execute(get);
					status = response.getStatusLine().getStatusCode();
					Log.i("requestGet", "Url: " + get.getURI().toASCIIString());
					Log.i("requestGet", "Status: " + status);
					if (HttpStatus.SC_OK == status) {
						response.getEntity().writeTo(cbb.getOutputStream());
					} else {

					}
				} catch (URISyntaxException e) {
					Log.e("requestGet","BackgroundProcess",e);
				} catch (ClientProtocolException e) {
					Log.e("requestGet","BackgroundProcess",e);
				} catch (IOException e) {
					Log.e("requestGet","BackgroundProcess",e);

				} finally {
					try {
						cbb.getOutputStream().close();
					} catch (IOException e) {
						Log.e("requestGet","BackgroundProcess",e);
					}
				}
			}
		});
		t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void uncaughtException(Thread thread, Throwable ex) {
				// TODO 自動生成されたメソッド・スタブ
				Log.e("requestGet","BackgroundProcessError",ex);

			}
		});
		t.run();
		return cbb.getInputStream();
	}

}
