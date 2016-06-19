package jp.redmine.redmineclient.task;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jp.redmine.redmineclient.entity.IPostingRecord;


abstract public class SelectDataPost<X,Y extends IPostingRecord> extends SelectDataTask<X,Y> {

	protected SelectDataTaskPutHandler getPutHandler(final Y item){
		return new SelectDataTaskPutHandler() {

			@Override
			public void getContent(HttpURLConnection con) throws ParserConfigurationException, TransformerException, IOException {
				DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
				Document document = dbuilder.newDocument();
				Element root = item.getXml(document);
				document.appendChild(root);

				TransformerFactory transFactory = TransformerFactory.newInstance();
				Transformer transformer = transFactory.newTransformer();
				con.setRequestProperty("Content-Type", "application/xml");
				transformer.transform(new DOMSource(document), new StreamResult(con.getOutputStream()));

			}
		};
	}
}
