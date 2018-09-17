package jp.redmine.redmineclient.form.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.entity.TypeConverter;

class RedmineConvertToHtmlHelper {
	static public final String URL_PREFIX = BuildConfig.APPLICATION_ID + BuildConfig.VERSION_CODE +"://";
	private int connection_id;
	private long project_id;
	HashMap<WikiType, ConvertToHtmlHelper> helpers = new HashMap<WikiType, ConvertToHtmlHelper>();
	RefugeText pre = new RefugeTextPre();
	RefugeTextMacroInclude macroInclude = new RefugeTextMacroInclude();

	RefugeTextInlineUrl url = new RefugeTextInlineUrl() {
		@Override
		protected String pull(Anchor input) {
			return HtmlHelper.getAnchor(input.label, input.url);
		}
	};
	RefugeTextWiki wiki = new RefugeTextWiki() {
		@Override
		protected String pull(Anchor input) {
			return HtmlHelper.getAnchor(input.label
					, URL_PREFIX, "wiki/", String.valueOf(connection_id), "/", String.valueOf(project_id), "/", input.url);
		}
	};
	RefugeTextIssue issue = new RefugeTextIssue() {
		@Override
		protected String pull(Anchor input) {
			return HtmlHelper.getAnchor(input.label
					, URL_PREFIX, "issue/", String.valueOf(connection_id), "/", input.url);
		}

	};

	public RedmineConvertToHtmlHelper(){
		helpers.put(WikiType.Markdown, new MarkdownHelper());
		helpers.put(WikiType.Texttile, new TextileHelper());
		helpers.put(WikiType.None, new ConvertToHtmlHelper() {
			@Override
			public String getHtml(String input) {
				return input;
			}
		});
	}

	public String parse(String input, WikiType type, int connectionid, long project){
		String export = beforeParse(input);
		export = parse(export,type);
		export = afterParse(export, connectionid, project);
		return export;
	}

	public String parse(String input, WikiType type){
		return helpers.get(type).getHtml(input);
	}

	public String beforeParse(String input) {
		input = pre.refuge(input); //must first
		input = macroInclude.refuge(input);
		input = macroInclude.restore(input);
		input = pre.refugeadd(input); //support for expand include
		input = url.refuge(input);
		input = issue.refuge(input);
		input = wiki.refuge(input);
		return input;
	}

	public String afterParse(String input, int connectionid, long project) {
		this.connection_id = connectionid;
		this.project_id = project;
		input = url.restore(input);
		input = wiki.restore(input);
		input = issue.restore(input);
		input = pre.restore(input); //must last
		input = input.replace("{{fnlist}}",""); //support for wiki extension
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
			action.issue(TypeConverter.parseInteger(items[1], -1), TypeConverter.parseInteger(items[2], -1));
			return true;
		} else if("wiki".equals(items[0]) && cnt >= 3){
			StringBuilder pagetitle = new StringBuilder();
			for(int i = 3; i < items.length; i++)
				pagetitle.append(items[i]);
			action.wiki(TypeConverter.parseInteger(items[1], -1), TypeConverter.parseInteger(items[2], -1), pagetitle.toString());
			return true;
		}
		return false;
	}

}
