package jp.redmine.redmineclient.form.helper;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefugeTextWiki extends RefugeText<Anchor>{
	@Override
	protected Pattern getPattern() {
		return Pattern.compile("\\[\\[([^\\]\\|]+?)(\\|([^\\]\\|]+?))?\\]\\]");
	}

	@Override
	protected String pull(Anchor input) {
		return input.label;
	}

	@Override
	protected Anchor push(Matcher m) {
		String target = m.group(1);
		String label = target;
		if(m.groupCount() > 2 && !TextUtils.isEmpty(m.group(3)))
			label = m.group(3);

		return new Anchor(target.replace(" ","_").replace("/",""), label);
	}

}
