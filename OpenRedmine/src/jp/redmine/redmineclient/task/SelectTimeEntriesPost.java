package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import jp.redmine.redmineclient.BuildConfig;
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
					DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
					Document document = dbuilder.newDocument();
					Element root = item.getXml(document);
					document.appendChild(root);

					TransformerFactory transFactory = TransformerFactory.newInstance();
					Transformer transformer = transFactory.newTransformer();
					StringWriter writer = new StringWriter();
					transformer.transform(new DOMSource(document), new StreamResult(writer));
					String data = writer.toString();
					StringEntity  entity = new StringEntity(data,"UTF-8");
					entity.setContentType("application/xml");

					if(BuildConfig.DEBUG) Log.d("post",entity.getContent().toString());
					return entity;
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
