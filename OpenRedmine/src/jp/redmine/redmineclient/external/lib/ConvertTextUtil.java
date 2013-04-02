package jp.redmine.redmineclient.external.lib;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.textile.TextileDialect;

import org.apache.commons.lang3.RandomStringUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;

public class ConvertTextUtil {

	static protected String reduceExternalHtml(String input, HashMap<String,String> export){
		Pattern p = Pattern.compile("<\\s*pre\\s*>(.*)<\\s*/\\s*pre\\s*>", Pattern.DOTALL);
		String texttile = input;
		Matcher m = p.matcher(texttile);

		while(m.find()){
			String target = m.group(1);
			StringBuffer sb = new StringBuffer();
			if(!TextUtils.isEmpty(target)){
				sb.append("<div class=\"pre\">");
				sb.append(target
						.replaceAll("&", "&amp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						.replaceAll("[\\r\\n]+", "<br>\r\n")
						.replaceAll(" ", "&nbsp;")
						.replaceAll("	", "&#009;")
						);
				sb.append("</div>");
			}
			String key = RandomStringUtils.randomAlphabetic(10);
			export.put(key, sb.toString());
			texttile = m.replaceFirst(key);
			m = p.matcher(texttile);
		}
		return texttile;
	}
	static public String convertTextileToHtml(String text){
		return convertTextileToHtml(text, false);
	}
	static public String convertTextileToHtml(String text, boolean isDocument){
		HashMap<String,String> restore = new HashMap<String,String>();
		String textile = reduceExternalHtml(text,restore);
		StringWriter sw = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(sw);
		builder.setEmitAsDocument(isDocument);

		MarkupParser parser = new MarkupParser(new TextileDialect());
		parser.setBuilder(builder);
		parser.parse(textile);
		textile = sw.toString();
		for(String item : restore.keySet()){
			textile = textile.replace(item, restore.get(item));
		}
		return  textile;
	}
	static public String getHtml(Context context,String innerhtml,String headerhtml){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" ?>");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		sb.append("<head>");
		sb.append(headerhtml);
		sb.append("</head>");
		sb.append("<body");
		sb.append(" bgcolor=\"#" + getRGBString(getBackgroundColor(context)) + "\"");
		sb.append(" text=\"#" + getRGBString(getFontColor(context)) + "\"");
		sb.append(" style=\"margin:0;\"");
		sb.append(">");
		sb.append(innerhtml);
		sb.append("</body></html>");
		return sb.toString();
	}

	static public int getAttribute(Context context,int target){
		TypedValue typedValue = new TypedValue();
		context.getTheme().resolveAttribute(target, typedValue, true);
		int resourceId = typedValue.resourceId;
		return context.getResources().getColor(resourceId);
	}
	static public String getRGBString(int color){
		String hex = Integer.toHexString(0xFF000000 | color);
		return hex.substring(hex.length()-6);
	}
	static public int getBackgroundColor(Context context){
		return getAttribute(context, android.R.attr.colorBackground);
	}
	static public int getFontColor(Context context){
		return getAttribute(context, android.R.attr.colorForeground);
	}
}
