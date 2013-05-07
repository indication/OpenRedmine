package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.url.RemoteUrlTimeEntries;

public class SelectTimeEntriesPost extends SelectDataTask<Void,RedmineTimeEntry> {

	protected DatabaseCacheHelper helper;
	protected RedmineConnection connection;
	public SelectTimeEntriesPost(DatabaseCacheHelper helper,RedmineConnection con){
		this.helper = helper;
		this.connection = con;
	}


	public SelectTimeEntriesPost() {
	}

	@Override
	protected Void doInBackground(RedmineTimeEntry... params) {
		SelectDataTaskDataHandler handler = new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
			}
		};

		SelectDataTaskConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		RemoteUrlTimeEntries url = new RemoteUrlTimeEntries();
		for(final RedmineTimeEntry item : params){
			SelectDataTaskPutHandler puthandler = new SelectDataTaskPutHandler() {

				@Override
				public HttpEntity getContent() throws IOException,
						SQLException, IllegalArgumentException, ParserConfigurationException, TransformerException {
					List<NameValuePair> data = new ArrayList<NameValuePair>();
					data.add(new BasicNameValuePair("xml", item.getXml()));
					Log.d("xml",item.getXml());
					Log.d("postdata",data.toString());
					return new UrlEncodedFormEntity(data, "UTF-8");
				}
			};
			if(item.getTimeentryId() == null){
				url.setId(null);

				postData(client, connection, url, handler, puthandler);
			} else {
				url.setId(item.getTimeentryId());
				putData(client, connection, url, handler, puthandler);
			}
		}
		client.close();
		return null;
	}

	@Override
	protected void onErrorRequest(int statuscode) {

	}

	@Override
	protected void onProgress(int max, int proc) {

	}

}
