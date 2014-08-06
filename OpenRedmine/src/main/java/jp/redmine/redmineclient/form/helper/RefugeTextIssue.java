package jp.redmine.redmineclient.form.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefugeTextIssue extends RefugeText<Anchor>{
	@Override
	protected Pattern getPattern() {
		return Pattern.compile("\\[\\[#(\\d+)\\]\\]|#(\\d+)");
	}

	@Override
	protected String pull(Anchor input) {
		return input.label;
	}

	@Override
	protected Anchor push(Matcher m) {
		return new Anchor(m.group(1) == null ? m.group(2) : m.group(1),m.group());
	}

}
