package jp.redmine.redmineclient.task;

import java.io.IOException;
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

import android.util.Log;
import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.entity.IPostingRecord;


abstract public class SelectDataPost<X,Y extends IPostingRecord> extends SelectDataTask<X,Y> {

	protected SelectDataTaskPutHandler getPutHandler(final Y item){
		return new SelectDataTaskPutHandler() {

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
	}
}
