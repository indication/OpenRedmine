package jp.redmine.redmineclient.form.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefugeTextInlineUrl extends RefugeText<String>{
	@Override
	protected Pattern getPattern() {
		return Pattern.compile(
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
	}

	@Override
	protected String pull(String input) {
		return input;
	}

	@Override
	protected String push(Matcher m) {
		return m.group();
	}

}
