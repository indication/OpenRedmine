package jp.redmine.redmineclient.form;

import java.util.Date;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineUser;

import com.andreabaccega.widget.FormEditText;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

abstract public class FormHelper {
	public static CharSequence convertWikiString(String str){
		return Html.fromHtml(str);
	}


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
	protected void performSetVisible(ViewGroup form,final boolean flag){
		performEachView(form,new EventPerformEachView() {
			@Override
			void callback(View item) {
				performSetVisible(item,flag);
			}
		});
	}
	protected void performSetVisible(View item,final boolean flag){
		item.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void performSetEnabled(ViewGroup form,final boolean flag){
		performEachView(form,new EventPerformEachView() {
			@Override
			void callback(View item) {
				performSetEnabled(item,flag);
			}
		});
	}
	protected void performSetEnabled(View item,final boolean flag){
		item.setEnabled(flag);
	}
	protected void performEachView(ViewGroup form,EventPerformEachView event){
		for(int idx = 0; idx < form.getChildCount(); idx++){
			View item = form.getChildAt(idx);
			if(item instanceof ViewGroup){
				performEachView((ViewGroup)item,event);
			} else {
				event.callback(item);
			}
		}
	}

	abstract class EventPerformEachView{
		abstract void callback(View item);
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
			if(!ValidateForm(item,result && isForcus)){
				result = false;
			}
		}
		return result;
	}



	/**
	 * Convert username by text format
	 * @param form view
	 * @param us User
	 * @return User name
	 */
	protected String convertUserName(View form,RedmineUser us){
		if(us == null)
			return "";
		return form.getContext().getString(R.string.format_name, us.getName(), us.getLoginName());
	}
	/**
	 * Set ct to v
	 * @param v TextView to set user name
	 * @param ct User name
	 */
	protected void setUserName(TextView v,RedmineUser ct){
		v.setText(convertUserName(v,ct));
	}


	protected String convertDate(View form,Date date){
		if(date == null)
			return "";
		return form.getContext().getString(R.string.format_date, date);
	}

	/**
	 * Set ct to v
	 * @param v TextView to set user name
	 * @param ct User name
	 */
	protected void setDate(TextView v,Date date){
		v.setText(convertDate(v,date));
	}
}
