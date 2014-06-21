package jp.redmine.redmineclient.form.helper;

import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.textile.TextileDialect;

import java.io.StringWriter;

public class TextileHelper {


	static public String convertTextileToHtml(String text, ConvertToHtmlHelper helper){
		return convertTextileToHtml(text, false, helper);
	}
	static public String convertTextileToHtml(String text, boolean isDocument, ConvertToHtmlHelper helper){

		String textile = text;
		if(helper != null)
			textile = helper.beforeParse(textile);
		StringWriter sw = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(sw);
		builder.setEmitAsDocument(isDocument);

		MarkupParser parser = new MarkupParser(new TextileDialect());
		parser.setBuilder(builder);
		parser.parse(textile);
		textile = sw.toString();
		if(helper != null)
			textile = helper.afterParse(textile);
		return  textile;
	}

}
