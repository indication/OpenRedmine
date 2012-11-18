package jp.redmine.redmineclient.external.lib;

import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.params.HttpParams;

public class GzipHttpResponse implements HttpResponse {
	private HttpResponse response;

	public GzipHttpResponse(HttpResponse response) {
		this.response = response;
	}

	@Override
	public HttpEntity getEntity() {
		return new GzipHttpEntity(response.getEntity());
	}
	@Override
	public void addHeader(Header header) {
		response.addHeader(header);
	}

	@Override
	public void addHeader(String name, String value) {
		response.addHeader(name, value);
	}

	@Override
	public boolean containsHeader(String name) {
		return response.containsHeader(name);
	}

	@Override
	public Header[] getAllHeaders() {
		return response.getAllHeaders();
	}

	@Override
	public Header getFirstHeader(String name) {
		return response.getFirstHeader(name);
	}

	@Override
	public Header[] getHeaders(String name) {
		return response.getHeaders(name);
	}

	@Override
	public Header getLastHeader(String name) {
		return response.getLastHeader(name);
	}

	@Override
	public HttpParams getParams() {
		return response.getParams();
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		return response.getProtocolVersion();
	}

	@Override
	public HeaderIterator headerIterator() {
		return response.headerIterator();
	}

	@Override
	public HeaderIterator headerIterator(String name) {
		return response.headerIterator(name);
	}

	@Override
	public void removeHeader(Header header) {
		response.removeHeader(header);
	}

	@Override
	public void removeHeaders(String name) {
		response.removeHeaders(name);

	}

	@Override
	public void setHeader(Header header) {
		response.setHeader(header);
	}

	@Override
	public void setHeader(String name, String value) {
		response.setHeader(name, value);
	}

	@Override
	public void setHeaders(Header[] headers) {
		response.setHeaders(headers);
	}

	@Override
	public void setParams(HttpParams params) {
		response.setParams(params);
	}

	@Override
	public Locale getLocale() {
		return response.getLocale();
	}

	@Override
	public StatusLine getStatusLine() {
		return response.getStatusLine();
	}

	@Override
	public void setEntity(HttpEntity entity) {
		response.setEntity(entity);
	}

	@Override
	public void setLocale(Locale loc) {
		response.setLocale(loc);
	}

	@Override
	public void setReasonPhrase(String reason) throws IllegalStateException {
		response.setReasonPhrase(reason);
	}

	@Override
	public void setStatusCode(int code) throws IllegalStateException {
		response.setStatusCode(code);
	}

	@Override
	public void setStatusLine(StatusLine statusline) {
		response.setStatusLine(statusline);
	}

	@Override
	public void setStatusLine(ProtocolVersion ver, int code) {
		response.setStatusLine(ver, code);
	}

	@Override
	public void setStatusLine(ProtocolVersion ver, int code, String reason) {
		response.setStatusLine(ver, code, reason);
	}

}
