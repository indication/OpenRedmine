package jp.redmine.redmineclient.external;

import java.io.IOException;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.parser.BaseParser;
import jp.redmine.redmineclient.url.RemoteUrl;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class Fetcher<T> {
	private RemoteUrl remoteurl;
	private XmlPullParser xmlPullParser;
	private BaseParser<T,?> parser;

	public boolean fetchData(RedmineConnection connectinfo,T data)
	throws XmlPullParserException, IOException {
		Connection con = new Connection(connectinfo);
		xmlPullParser =  Xml.newPullParser();
		con.setupRemoteUrl(remoteurl);
		xmlPullParser.setInput(con.requestGet(), "UTF-8");
		parser.setXml(xmlPullParser);
		parser.parse(data);
		return true;
	}


	public String fetchError() throws XmlPullParserException, IOException {
		int state;
		StringBuilder sb = new StringBuilder();

		while((state = xmlPullParser.next())== XmlPullParser.END_DOCUMENT)
		{
			if(state==XmlPullParser.START_TAG
					&& xmlPullParser.getName().toLowerCase() == "error"){
				sb.append(xmlPullParser.getText());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * @param remoteurl セットする remoteurl
	 */
	public void setRemoteurl(RemoteUrl remoteurl) {
		this.remoteurl = remoteurl;
	}

	/**
	 * @return remoteurl
	 */
	public RemoteUrl getRemoteurl() {
		return remoteurl;
	}

	/**
	 * @param parser セットする parser
	 */
	public void setParser(BaseParser<T,?> parser) {
		this.parser = parser;
	}
	/**
	 * @return parser
	 */
	public BaseParser<T,?> getParser() {
		return parser;
	}
}
