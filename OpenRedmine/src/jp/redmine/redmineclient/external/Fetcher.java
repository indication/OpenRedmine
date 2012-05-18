package jp.redmine.redmineclient.external;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class Fetcher {
	private RemoteUrl remoteurl;
	private XmlPullParser xmlPullParser = Xml.newPullParser();
	private int status = 0;
	public boolean fetchData() throws Throwable{

		URL url = new URL(remoteurl.getUrl().toString());

		URLConnection connection = url.openConnection();
		status = connection.getHeaderFieldInt("Status", 0);
		if(status == 200){
			xmlPullParser.setInput(connection.getInputStream(), "UTF-8");
		} else if(isStatusSuccess(status)) {
			// do nothing -- there is no data
		} else {
			xmlPullParser.setInput(connection.getInputStream(), "UTF-8");
			return false;
		}
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

	protected boolean isStatusSuccess(int status){
		// return true with 2xx
		return ((int)(status / 100) == 2);
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
}
