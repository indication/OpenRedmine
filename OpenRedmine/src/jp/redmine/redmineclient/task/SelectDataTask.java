package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParserException;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.external.lib.AuthenticationParam;
import jp.redmine.redmineclient.external.lib.ClientParam;
import jp.redmine.redmineclient.external.lib.ConnectionHelper;
import jp.redmine.redmineclient.url.RemoteUrl;
import jp.redmine.redmineclient.url.RemoteUrl.requests;
import jp.redmine.redmineclient.url.RemoteUrl.versions;

public abstract class SelectDataTask<T> extends AsyncTask<Integer, Integer, List<T>> {
	/**
	 * Notify error request on UI thread
	 * @param statuscode http response code
	 */
	abstract protected void onErrorRequest(int statuscode);
	/**
	 * Notify progress on UI thread
	 * @param max total count of the items
	 * @param proc current count of the items
	 */
	abstract protected void onProgress(int max,int proc);
	/**
	 * Store the last exception (reference by UI thread)
	 */
	private volatile Exception lasterror;

	interface ProgressKind{
		public int progress = 1;
		public int error = 2;
		public int unknown = 3;
	}
	@Override
	protected final void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		switch(values[0]){
		case ProgressKind.progress:
			onProgress(values[1],values[2]);
			break;
		case ProgressKind.error:
			onErrorRequest(values[1]);
			break;
		case ProgressKind.unknown:
			onError(lasterror);
			break;
		default:
		}
	}
	protected void onError(Exception lasterror){
		Log.e("SelectDataTask", "background", lasterror);
	}

	protected void publishProgress(int max,int proc){
		super.publishProgress(ProgressKind.progress,max,proc);
	}

	protected void publishErrorRequest(int status){
		super.publishProgress(ProgressKind.error,status);
	}
	protected void publishError(Exception e){
		lasterror = e;
		super.publishProgress(ProgressKind.unknown);
	}

	protected void helperAddItems(ArrayAdapter<T> listAdapter,List<T> items){
		if(items == null)
			return;
		listAdapter.notifyDataSetInvalidated();
		for (T i : items){
			listAdapter.add(i);
		}
		listAdapter.notifyDataSetChanged();
	}

	private ClientParam getClientParam(Uri remoteurl){
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

	private boolean isGZipHttpResponse(HttpResponse response) {
		Header header = response.getEntity().getContentEncoding();
		if (header == null) return false;
		String value = header.getValue();
		return (!TextUtils.isEmpty(value) && value.contains("gzip"));
	}
	protected void fetchData(RedmineConnection connection,RemoteUrl url,SelectDataTaskDataHandler<RedmineConnection> handler){
		url.setupRequest(requests.xml);
		url.setupVersion(versions.v130);
		fetchData(connection, url.getUrl(connection.getUrl()),handler);
	}

	protected void fetchData(RedmineConnection connection,Builder builder,SelectDataTaskDataHandler<RedmineConnection> handler){
		Uri remoteurl = builder.build();
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
		try {
			HttpGet get = new HttpGet(new URI(remoteurl.toString()));
			get.setHeader("X-Redmine-API-Key", connection.getToken());
			get.setHeader("Accept-Encoding", "gzip, deflate");
			HttpResponse response = client.execute(get);
			int status = response.getStatusLine().getStatusCode();
			Log.i("requestGet", "Url: " + get.getURI().toASCIIString());
			Log.i("requestGet", "Status: " + status);
			if (HttpStatus.SC_OK == status) {
				InputStream stream = response.getEntity().getContent();
				if (isGZipHttpResponse(response)) {
					Log.i("requestGet", "Gzip: Enabled");
					stream =  new GZIPInputStream(stream);
				}
				handler.onContent(connection,stream);
			} else {
				publishErrorRequest(status);
			}
		} catch (URISyntaxException e) {
			publishErrorRequest(404);
		} catch (ClientProtocolException e) {
			publishError(e);
		} catch (IOException e) {
			publishError(e);
		} catch (XmlPullParserException e) {
			publishError(e);
		} finally {
			client.getConnectionManager().shutdown();
		}
	}
}
