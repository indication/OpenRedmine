package jp.redmine.redmineclient.form.helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.textile.TextileDialect;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.entity.TypeConverter;

public class TextileHelper {
	static public final String URL_PREFIX = BuildConfig.PACKAGE_NAME + BuildConfig.VERSION_CODE +"://";
	private Pattern patternIntent = Pattern.compile(URL_PREFIX);
	private Pattern patternIssue = Pattern.compile("#(\\d+)([^;\\d]|$)");
	private Pattern patternWiki = Pattern.compile("\\[\\[([^\\]\\|]+?)\\]\\]");
	private Pattern patternWikiAnchor = Pattern.compile("\\[\\[([^\\]\\|]+?)\\|([^\\]\\|]+?)\\]\\]");
	private Pattern patternInlineUrl = Pattern.compile(
			"\\b((" +
			//START inherits from http://www.din.or.jp/~ohzaki/perl.htm#URI
			"(?:https?|shttp)://(?:(?:[-_.!~*'()a-zA-Z0-9;:&=+$,]|%[0-9A-Fa-f" +
			"][0-9A-Fa-f])*@)?(?:(?:[a-zA-Z0-9](?:[-a-zA-Z0-9]*[a-zA-Z0-9])?\\.)" +
			"*[a-zA-Z](?:[-a-zA-Z0-9]*[a-zA-Z0-9])?\\.?|[0-9]+\\.[0-9]+\\.[0-9]+\\." +
			"[0-9]+)(?::[0-9]*)?(?:/(?:[-_.!~*'()a-zA-Z0-9:@&=+$,]|%[0-9A-Fa-f]" +
			"[0-9A-Fa-f])*(?:;(?:[-_.!~*'()a-zA-Z0-9:@&=+$,]|%[0-9A-Fa-f][0-9A-" +
			"Fa-f])*)*(?:/(?:[-_.!~*'()a-zA-Z0-9:@&=+$,]|%[0-9A-Fa-f][0-9A-Fa-f" +
			"])*(?:;(?:[-_.!~*'()a-zA-Z0-9:@&=+$,]|%[0-9A-Fa-f][0-9A-Fa-f])*)*)" +
			"*)?(?:\\?(?:[-_.!~*'()a-zA-Z0-9;/?:@&=+$,]|%[0-9A-Fa-f][0-9A-Fa-f])" +
			"*)?(?:#(?:[-_.!~*'()a-zA-Z0-9;/?:@&=+$,]|%[0-9A-Fa-f][0-9A-Fa-f])*" +
			")?" +
			")|(" +	 // ftp section
			"s?ftps?://(?:(?:[-_.!~*'()a-zA-Z0-9;&=+$,]|%[0-9A-Fa-f][0-9A-Fa-f])*" +
			"(?::(?:[-_.!~*'()a-zA-Z0-9;&=+$,]|%[0-9A-Fa-f][0-9A-Fa-f])*)?@)?(?" +
			":(?:[a-zA-Z0-9](?:[-a-zA-Z0-9]*[a-zA-Z0-9])?\\.)*[a-zA-Z](?:[-a-zA-" +
			"Z0-9]*[a-zA-Z0-9])?\\.?|[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)(?::[0-9]*)?" +
			"(?:/(?:[-_.!~*'()a-zA-Z0-9:@&=+$,]|%[0-9A-Fa-f][0-9A-Fa-f])*(?:/(?" +
			":[-_.!~*'()a-zA-Z0-9:@&=+$,]|%[0-9A-Fa-f][0-9A-Fa-f])*)*(?:;type=[" +
			"AIDaid])?)?(?:\\?(?:[-_.!~*'()a-zA-Z0-9;/?:@&=+$,]|%[0-9A-Fa-f][0-9" +
			"A-Fa-f])*)?(?:#(?:[-_.!~*'()a-zA-Z0-9;/?:@&=+$,]|%[0-9A-Fa-f][0-9A" +
			"-Fa-f])*)?" +
			//END
			"))"
			);
	//private Pattern patternDocuments = Pattern.compile("document:\\d+");
	private WebviewActionInterface action;
	public void setAction(WebviewActionInterface act){
		action = act;
	}
	public void setup(WebView view){
		setupWebView(view);
		setupHandler(view);
	}

	protected void setupWebView(WebView view){
		view.getSettings().setPluginState(PluginState.OFF);
		view.getSettings().setBlockNetworkLoads(true);
	}

	protected void setupHandler(WebView view){
		view.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Matcher m = patternIntent.matcher(url);
				if(m.find()){
					return kickAction(m.replaceAll(""));
				} else if (action != null) {
					return action.url(url);
				} else {
					return super.shouldOverrideUrlLoading(view, url);
				}
			}
		});
	}

	protected boolean kickAction(String urlpath){
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

	protected String  extendHtml(int connection_id,long projectid,String input){
		return extendHtml(String.valueOf(connection_id),String.valueOf(projectid),input);
	}
	protected String  extendHtml(String connection,String project,String input){
		String result = "";
		result = patternInlineUrl.matcher(input).replaceAll(getAnchor("$1","$1"));
		result = patternIssue.matcher(result).replaceAll(getAnchor("#$1",URL_PREFIX,"issue/",connection,"/","$1")+"$2");
		result = patternWikiAnchor.matcher(result).replaceAll(getAnchor("$2",URL_PREFIX,"wiki/",connection,"/",project,"/","$1"));
		result = patternWiki.matcher(result).replaceAll(getAnchor("$1",URL_PREFIX,"wiki/",connection,"/",project,"/","$1"));
		return result;
	}

	protected String getAnchor(String name,String... params){
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

	public void setContent(WebView view, final int connectionid, final long project, final String text){
		if (text == null)
			return;
		String inner = convertTextileToHtml(text, new ConvertToHtmlHelper() {
			@Override
			public String beforeParse(String input) {
				return extendHtml(connectionid,project,input);
			}

			@Override
			public String afterParse(String input) {
				return input;
			}
		});
		view.loadDataWithBaseURL("", getHtml(view.getContext(),inner,""), "text/html", "UTF-8", "");

	}

	public void setContent(WebView view, String text){
		String inner = convertTextileToHtml(text, null);
		view.loadDataWithBaseURL("", getHtml(view.getContext(),inner,""), "text/html", "UTF-8", "");
	}


	static protected String reduceExternalHtml(String input, HashMap<String,String> export){
		Pattern p = Pattern.compile("<\\s*pre\\s*>(.+?)<\\s*/\\s*pre\\s*>", Pattern.DOTALL);
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
	static public String convertTextileToHtml(String text, ConvertToHtmlHelper helper){
		return convertTextileToHtml(text, false, helper);
	}
	static public String convertTextileToHtml(String text, boolean isDocument, ConvertToHtmlHelper helper){
		HashMap<String,String> restore = new HashMap<String,String>();
		String textile = reduceExternalHtml(text,restore);
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
		for(String item : restore.keySet()){
			textile = textile.replace(item, restore.get(item));
		}
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
