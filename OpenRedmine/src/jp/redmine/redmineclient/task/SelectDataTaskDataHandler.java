package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.xmlpull.v1.XmlPullParserException;

interface SelectDataTaskDataHandler<T> {
	public void onContent(T item,InputStream stream) throws XmlPullParserException, IOException, SQLException;
}
