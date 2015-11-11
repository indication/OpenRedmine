package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

interface SelectDataTaskPutHandler {
	void getContent(HttpURLConnection con) throws ParserConfigurationException, TransformerException, IOException;
}
