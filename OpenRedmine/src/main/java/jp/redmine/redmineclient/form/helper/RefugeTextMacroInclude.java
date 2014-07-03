package jp.redmine.redmineclient.form.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefugeTextMacroInclude extends RefugeText<Anchor>{
	@Override
	protected Pattern getPattern() {
		return Pattern.compile("\\{\\s*\\{\\s*include\\((.+)\\)\\s*\\}\\s*\\}",Pattern.CASE_INSENSITIVE);
	}

	@Override
	protected String pull(Anchor input) {
		return " [[" + input.url +"|" + input.label + "]] ";
	}

	@Override
	protected Anchor push(Matcher m) {
		String target = m.group(1);
		String label = target;
		return new Anchor(target.replace(" ","_").replace("/",""), label);
	}

}
