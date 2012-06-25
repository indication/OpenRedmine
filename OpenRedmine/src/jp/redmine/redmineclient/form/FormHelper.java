package jp.redmine.redmineclient.form;

import android.text.Html;

public class FormHelper {
	public static CharSequence convertWikiString(String str){
		return Html.fromHtml(str);
	}
}
