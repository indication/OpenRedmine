package jp.redmine.redmineclient.form.helper;

import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.textile.TextileDialect;

import java.io.StringWriter;

public class TextileHelper implements ConvertToHtmlHelper {

	private MarkupParser parser = new MarkupParser(new TextileDialect());

	@Override
	public String getHtml(String textile) {
		return getHtml(textile, false);
	}

	public String getHtml(String textile, boolean isDocument) {
		StringWriter sw = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(sw);
		builder.setEmitAsDocument(isDocument);

		parser.setBuilder(builder);
		parser.parse(textile);
		return sw.toString();
	}
}
