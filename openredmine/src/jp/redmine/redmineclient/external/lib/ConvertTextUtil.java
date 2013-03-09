package jp.redmine.redmineclient.external.lib;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.textile.TextileDialect;

import org.apache.commons.lang3.RandomStringUtils;

import android.util.Log;

public class ConvertTextUtil {

	static protected String reduceExternalHtml(String input, HashMap<String,String> export){
		Pattern p = Pattern.compile("(?m)<\\s*pre\\s*>.*<\\s*/\\s*pre\\s*>");
		String texttile = input;
		Matcher m = p.matcher(texttile);
		while(m.find()){
			String target = m.group();
			String key = RandomStringUtils.randomAlphabetic(10);
			export.put(key, target);
			texttile = m.replaceFirst(key);
			m =  p.matcher(texttile);
		}
		return texttile;
	}
	static public String convertTextileToHtml(String text){
		HashMap<String,String> restore = new HashMap<String,String>();
		Log.d("convertTextileToHtml input",text);
		String textile = reduceExternalHtml(text,restore);
		Log.d("convertTextileToHtml reduced",textile);
		StringWriter sw = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(sw);
		builder.setEmitAsDocument(false);

		MarkupParser parser = new MarkupParser(new TextileDialect());
		parser.setBuilder(builder);
		parser.parse(textile);
		textile = sw.toString();
		Log.d("convertTextileToHtml export",textile);
		for(String item : restore.keySet()){
			textile = textile.replace(item, restore.get(item));
		}
		Log.d("convertTextileToHtml return",textile);
		return  textile;
	}
}
