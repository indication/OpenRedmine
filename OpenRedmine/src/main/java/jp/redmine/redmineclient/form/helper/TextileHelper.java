package jp.redmine.redmineclient.form.helper;

import android.content.Context;
import android.util.TypedValue;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.textile.TextileDialect;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.entity.TypeConverter;

public class TextileHelper {
	static public final String URL_PREFIX = BuildConfig.PACKAGE_NAME + BuildConfig.VERSION_CODE +"://";
	private Pattern patternIntent = Pattern.compile(URL_PREFIX);
	private Pattern patternIssue = Pattern.compile("#(\\d+)([^;\\d]|$)");
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
		String result = input;
		result = patternIssue.matcher(result).replaceAll(getAnchor("#$1",URL_PREFIX,"issue/",connection,"/","$1")+"$2");
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
			RefugeTextInlineUrl url = new RefugeTextInlineUrl(){
				@Override
				protected String pull(String input) {
					return getAnchor(input,input);
				}
			};
			RefugeTextWiki wiki = new RefugeTextWiki(){
				@Override
				protected String pull(WikiAnchor input) {
					return getAnchor(input.label
							,URL_PREFIX,"wiki/",String.valueOf(connectionid),"/",String.valueOf(project),"/",input.title);
				}
			};
			@Override
			public String beforeParse(String input) {
				input = url.refuge(input);
				input = wiki.refuge(input);
				return extendHtml(connectionid,project,input);
			}

			@Override
			public String afterParse(String input) {
				input = url.restore(input);
				input = wiki.restore(input);
				return input;
			}
		});
		view.loadDataWithBaseURL("", getHtml(view.getContext(),inner,""), "text/html", "UTF-8", "");

	}

	public void setContent(WebView view, String text){
		String inner = convertTextileToHtml(text, null);
		view.loadDataWithBaseURL("", getHtml(view.getContext(),inner,""), "text/html", "UTF-8", "");
	}


	static public String convertTextileToHtml(String text, ConvertToHtmlHelper helper){
		return convertTextileToHtml(text, false, helper);
	}
	static public String convertTextileToHtml(String text, boolean isDocument, ConvertToHtmlHelper helper){
		RefugeText refugePre = new RefugeTextPre();

		String textile = refugePre.refuge(text);
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
		return  refugePre.restore(textile);
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
