package jp.redmine.redmineclient.form.helper;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RefugeText {
	HashMap<String,String> export = new HashMap<String, String>();

	abstract protected Pattern getPattern();
	protected String pull(String input){
		return input;
	}
	protected String push(Matcher m){
		return m.group();
	}

	public String refuge(String input){
		export.clear();
		Pattern p = getPattern();
		String texttile = input;
		Matcher m = p.matcher(texttile);

		while(m.find()){
			String key = RandomStringUtils.randomAlphabetic(10);
			export.put(key, push(m));
			texttile = m.replaceFirst(key);
			m = p.matcher(texttile);
		}
		return texttile;
	}

	public String restore(String input){
		if(export.isEmpty())
			return input;
		for(String item : export.keySet()){
			input = input.replace(item, pull(export.get(item)));
		}
		return input;
	}
}
