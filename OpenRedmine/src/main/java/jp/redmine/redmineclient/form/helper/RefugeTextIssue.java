package jp.redmine.redmineclient.form.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefugeTextIssue extends RefugeText<RefugeTextIssue.IssueAnchor>{
	class IssueAnchor {
		public String id;
		public String label;
		public IssueAnchor(String title,String strid){
			id = strid;
			label = label;
		}
	}
	@Override
	protected Pattern getPattern() {
		return Pattern.compile("\\b(#(\\d+))");
	}

	@Override
	protected String pull(IssueAnchor input) {
		return input.label;
	}

	@Override
	protected IssueAnchor push(Matcher m) {
		return new IssueAnchor(m.group(),m.group(1));
	}

}
