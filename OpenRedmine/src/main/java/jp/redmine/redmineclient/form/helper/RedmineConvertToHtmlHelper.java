package jp.redmine.redmineclient.form.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.entity.TypeConverter;

class RedmineConvertToHtmlHelper implements ConvertToHtmlHelper {
	static public final String URL_PREFIX = BuildConfig.PACKAGE_NAME + BuildConfig.VERSION_CODE +"://";
	private final int connection_id;
	private final long project_id;
	RefugeText pre;
	RefugeTextInlineUrl url;
	RefugeTextWiki wiki;
	RefugeTextIssue issue;

	public RedmineConvertToHtmlHelper(int connectionid, long project) {
		this.connection_id = connectionid;
		this.project_id = project;
		pre = new RefugeTextPre();
		url = new RefugeTextInlineUrl() {
			@Override
			protected String pull(Anchor input) {
				return HtmlHelper.getAnchor(input.label, input.url);
			}
		};
		wiki = new RefugeTextWiki() {
			@Override
			protected String pull(Anchor input) {
				return HtmlHelper.getAnchor(input.label
						, URL_PREFIX, "wiki/", String.valueOf(connection_id), "/", String.valueOf(project_id), "/", input.url);
			}
		};
		issue = new RefugeTextIssue() {
			@Override
			protected String pull(Anchor input) {
				return HtmlHelper.getAnchor(input.label
						, URL_PREFIX, "issue/", String.valueOf(connection_id), "/", input.url);
			}

		};
	}

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
	static public boolean kickAction(WebviewActionInterface action, String urlpath){
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

}
