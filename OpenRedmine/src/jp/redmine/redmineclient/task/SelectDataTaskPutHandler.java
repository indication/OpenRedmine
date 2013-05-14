package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.HttpEntity;

interface SelectDataTaskPutHandler {
	public HttpEntity getContent() throws IOException, SQLException, IllegalArgumentException, ParserConfigurationException, TransformerException;
}
