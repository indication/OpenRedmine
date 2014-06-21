package jp.redmine.redmineclient.form.helper;

import android.util.Log;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;

public class MarkdownHelper {
	private static final String TAG = MarkdownHelper.class.getSimpleName();

	static public String convertMarkdownToHtml(String text, ConvertToHtmlHelper helper){
		return convertMarkdownToHtml(text, false, helper);
	}
	static public String convertMarkdownToHtml(String text, boolean isDocument, ConvertToHtmlHelper helper){

		String textile = text;
		if(helper != null)
			textile = helper.beforeParse(textile);
		try {
			Markdown4jProcessor proc = new Markdown4jProcessor();
			textile = proc.process(textile);
		} catch (IOException e) {
			Log.e(TAG, "convertMarkdownToHtml", e);
		}
		if(helper != null)
			textile = helper.afterParse(textile);
		return  textile;
	}

}
