package jp.redmine.redmineclient.form.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefugeTextPre extends RefugeText<String>{
	@Override
	protected Pattern getPattern() {
		return Pattern.compile("<\\s*pre\\s*>(.+?)<\\s*/\\s*pre\\s*>", Pattern.DOTALL);
	}

	@Override
	protected String push(Matcher m) {
		return m.group(1);
	}

	@Override
	protected String pull(String input) {
		StringBuffer sb = new StringBuffer();
		sb.append("<div class=\"pre\">");
		sb.append(input
						.replaceAll("&", "&amp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						.replaceAll("[\\r\\n]+", "<br>\r\n")
						.replaceAll(" ", "&nbsp;")
						.replaceAll("	", "&#009;")
		);
		sb.append("</div>");
		return sb.toString();
	}
}
