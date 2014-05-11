package jp.redmine.redmineclient.form.helper;

import android.content.Context;
import android.util.TypedValue;

import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.textile.TextileDialect;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.entity.TypeConverter;

public class TextileHelper {
	static public final String URL_PREFIX = BuildConfig.PACKAGE_NAME + BuildConfig.VERSION_CODE +"://";
	//private Pattern patternDocuments = Pattern.compile("document:\\d+");
	static protected boolean kickAction(WebviewActionInterface action, String urlpath){
		String encodeStr = urlpath;
		try {
			encodeStr = URLDecoder.decode(urlpath, "utf-8");
		} catch (UnsupportedEncodingException e) {

		}
		String[] items = encodeStr.split("/");
		int cnt = items.length;
		if(cnt < 3 || action == null){
			/* do nothing */
		} else if("issue".equals(items[0])){
			action.issue(TypeConverter.parseInteger(items[1]), TypeConverter.parseInteger(items[2]));
			return true;
		} else if("wiki".equals(items[0]) && cnt >= 3){
			StringBuilder pagetitle = new StringBuilder();
			for(int i = 3; i < items.length; i++)
				pagetitle.append(items[i]);
			action.wiki(TypeConverter.parseInteger(items[1]), TypeConverter.parseInteger(items[2]), pagetitle.toString());
			return true;
		}
		return false;
	}

	static protected String getAnchor(String name,String... params){
		StringBuffer sb = new StringBuffer();
		sb.append("<a href=\"");
		for(String item : params){
			sb.append(item);
		}
		sb.append("\">");
		sb.append(name);
		sb.append("</a>");
		return sb.toString();
	}

	static public String getHtml(final int connectionid, final long project, final String text){
		if (text == null)
			return "";
		String inner = convertTextileToHtml(text, new ConvertToHtmlHelper() {
			RefugeText pre = new RefugeTextPre();
			RefugeTextInlineUrl url = new RefugeTextInlineUrl(){
				@Override
				protected String pull(Anchor input) {
					return getAnchor(input.label,input.url);
				}
			};
			RefugeTextWiki wiki = new RefugeTextWiki(){
				@Override
				protected String pull(Anchor input) {
					return getAnchor(input.label
							,URL_PREFIX,"wiki/",String.valueOf(connectionid),"/",String.valueOf(project),"/",input.url);
				}
			};
			RefugeTextIssue issue = new RefugeTextIssue(){
				@Override
				protected String pull(Anchor input) {
					return getAnchor(input.label
							,URL_PREFIX,"issue/",String.valueOf(connectionid),"/",input.url);
				}

			};
			@Override
			public String beforeParse(String input) {
				input = pre.refuge(input); //must first
				input = url.refuge(input);
				input = wiki.refuge(input);
				input = issue.refuge(input);
				return input;
			}

			@Override
			public String afterParse(String input) {
				input = url.restore(input);
				input = wiki.restore(input);
				input = issue.restore(input);
				input = pre.restore(input); //must last
				return input;
			}
		});
		return inner;
	}


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
	public interface ConvertToHtmlHelper {
		/**
		 *
		 * @param input TEXT string without reduced text (eg. pre)
		 * @return formatted string
		 */
		public String beforeParse(String input);

		/**
		 *
		 * @param input HTML string without reduced text (eg. pre)
		 * @return formatted string
		 */
		public String afterParse(String input);
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
