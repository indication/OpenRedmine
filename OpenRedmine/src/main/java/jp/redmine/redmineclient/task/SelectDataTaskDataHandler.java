package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.xmlpull.v1.XmlPullParserException;

public interface SelectDataTaskDataHandler {
	public void onContent(InputStream stream) throws XmlPullParserException, IOException, SQLException;
}
