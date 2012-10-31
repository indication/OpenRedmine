package jp.redmine.redmineclient.form;

import com.andreabaccega.widget.FormEditText;

import android.text.Html;

abstract public class FormHelper {
	public static CharSequence convertWikiString(String str){
		return Html.fromHtml(str);
	}

	abstract public void setup();

	public void setupEvents(){


	}

	public void setupDefaults(){
	}

	protected String cutoffString(String str,int cutoff){
		int limit = 0;
		String[] strs = str.split("[\r\n]+",cutoff+1);
		StringBuilder result = new StringBuilder();
		for(String item : strs){
			result.append(item);
			result.append("\r\n");
			limit++;
			if(limit >= cutoff){
				break;
			}
		}
		return result.toString();
	}


	public boolean Validate(){
		return true;
	}

	protected boolean ValidateForm(FormEditText item ){
		return ValidateForm(item,true);
	}

	protected boolean ValidateForm(FormEditText item,boolean isForcus){
		if(item.testValidity()){
			return true;
		} else {
			if(isForcus){
				item.requestFocus();
			}
			return false;
		}
	}
	protected boolean ValidateForms(FormEditText ... list ){
		return ValidateForms(list,true);
	}

	protected boolean ValidateForms(FormEditText[] list,boolean isForcus){
		boolean result = true;
		for(FormEditText item :list){
			if(ValidateForm(item,result && isForcus)){
				result = false;
			}
		}
		return result;
	}
}
