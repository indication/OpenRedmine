package jp.redmine.redmineclient.form.helper;

import android.text.TextUtils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RefugeText<T> {
	HashMap<String,T> export = new HashMap<String, T>();

	abstract protected Pattern getPattern();
	abstract protected String pull(T input);
	abstract protected T push(Matcher m);

	public String refuge(String input){
		export.clear();
		return refugeadd(input);
	}

	public String refugeadd(String input){
		if(TextUtils.isEmpty(input))
			return "";
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
