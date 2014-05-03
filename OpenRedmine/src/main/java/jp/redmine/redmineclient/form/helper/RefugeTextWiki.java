package jp.redmine.redmineclient.form.helper;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefugeTextWiki extends RefugeText<RefugeTextWiki.WikiAnchor>{
	public class WikiAnchor{
		public String title;
		public String label;
		public WikiAnchor(String tgt, String lbl){
			title = tgt;
			label = lbl;
		}
	}
	@Override
	protected Pattern getPattern() {
		return Pattern.compile("\\[\\[([^\\]\\|]+?)(\\|([^\\]\\|]+?))?\\]\\]");
	}

	@Override
	protected String pull(WikiAnchor input) {
		return input.label;
	}

	@Override
	protected WikiAnchor push(Matcher m) {
		String target = m.group(1);
		String label = target;
		if(m.groupCount() > 2 && !TextUtils.isEmpty(m.group(3)))
			label = m.group(3);

		return new WikiAnchor(target.replace(" ","_").replace("/",""), label);
	}

}
