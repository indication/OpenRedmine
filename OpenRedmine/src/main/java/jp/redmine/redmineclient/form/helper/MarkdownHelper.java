package jp.redmine.redmineclient.form.helper;

import android.util.Log;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;

public class MarkdownHelper implements ConvertToHtmlHelper {
	private static final String TAG = MarkdownHelper.class.getSimpleName();
	private Markdown4jProcessor proc = new Markdown4jProcessor();

	@Override
	public String getHtml(String text) {
		String textile = text;
		try {
			textile = proc.process(textile);
		} catch (IOException e) {
			Log.e(TAG, "convertMarkdownToHtml", e);
		}
		return  textile;
	}
}
