package jp.redmine.redmineclient.external.lib;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.text.TextUtils;

public class DefaultGzipHttpClient extends DefaultHttpClient {

	public <T> T execute(HttpUriRequest request, final ResponseHandler<? extends T> handler) {
			return super.execute(request, new ResponseHandler<T>() {
				@Override
				public T handleResponse(HttpResponse response) {
					try {
						if (isGZipHttpResponse(response)) {
							return handler.handleResponse(new GzipHttpResponse(response));
						} else {
							return handler.handleResponse(response);
						}
					} catch (ClientProtocolException e) {
						//throw new HttpException("",e);
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
			});
	}

	private boolean isGZipHttpResponse(HttpResponse response) {
		Header header = response.getEntity().getContentEncoding();
		if (header == null) return false;
		String value = header.getValue();
		return (!TextUtils.isEmpty(value) && value.contains("gzip"));
	}
}
