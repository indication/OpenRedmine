package jp.redmine.redmineclient.external.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class GzipHttpEntity implements HttpEntity {
	private HttpEntity entity;

	public GzipHttpEntity(HttpEntity entity) {
		this.entity = entity;
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return new GZIPInputStream(entity.getContent());
	}
	@Override
	public void consumeContent() throws IOException {
		entity.consumeContent();
	}

	@Override
	public Header getContentEncoding() {
		return entity.getContentEncoding();
	}

	@Override
	public long getContentLength() {
		return entity.getContentLength();
	}

	@Override
	public Header getContentType() {
		return entity.getContentType();
	}

	@Override
	public boolean isChunked() {
		return entity.isChunked();
	}

	@Override
	public boolean isRepeatable() {
		return entity.isRepeatable();
	}

	@Override
	public boolean isStreaming() {
		return entity.isStreaming();
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		//TODO extends gzip feature
		entity.writeTo(outstream);
	}

}
