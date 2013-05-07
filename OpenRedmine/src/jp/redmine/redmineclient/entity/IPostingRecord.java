package jp.redmine.redmineclient.entity;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public interface IPostingRecord {

	public String getXml() throws ParserConfigurationException, IllegalArgumentException, TransformerException;
}
