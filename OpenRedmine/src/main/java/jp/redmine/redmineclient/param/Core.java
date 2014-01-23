package jp.redmine.redmineclient.param;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

abstract class Core {
	private Bundle bundle;
	private Intent intent;
	public Bundle getArgument(){
		return this.bundle;
	}
	public void setArgument(){
		this.bundle = new Bundle();
	}
	public void setArgument(Bundle bundle){
		setArgument(bundle, false);
	}
	public void setArgument(Bundle bundle, boolean doClone){
		this.bundle = doClone ? (Bundle) bundle.clone() : bundle;
	}
	public Intent getIntent(){
		return this.intent;
	}
	public void setIntent(Intent intent){
		this.intent = intent;
	}
	public void setIntent(Context applicationContext, Class<?> cls){
		this.intent = new Intent(applicationContext, cls);
	}
	public void importArgument(Core arg) {
	}


	/**
	 * Set value to intent or bundle
	 * @param key key
	 * @param value int value
	 */
	protected void setArg(String key, Integer value){
		if(bundle != null)
			bundle.putInt(key, value);
		else if(intent != null)
			intent.putExtra(key, value);
	}

	/**
	 * Get value from intent or bundle
	 * @param key key
	 * @param defaultValue default value(nullable)
	 */
	protected Integer getArg(String key, Integer defaultValue){
		if(bundle != null)
			return bundle.containsKey(key) ? bundle.getInt(key) : defaultValue;
		else if(intent != null)
			return intent.hasExtra(key) ? intent.getIntExtra(key, 0) : defaultValue;
		else
			return defaultValue;
	}
	/**
	 * Set value to intent or bundle
	 * @param key key
	 * @param value long value
	 */
	protected void setArg(String key, Long value){
		if(bundle != null)
			bundle.putLong(key, value);
		else if(intent != null)
			intent.putExtra(key, value);
	}

	/**
	 * Get value from intent or bundle
	 * @param key key
	 * @param defaultValue default value(nullable)
	 */
	protected Long getArg(String key, Long defaultValue){
		if(bundle != null)
			return bundle.containsKey(key) ? bundle.getLong(key) : defaultValue;
		else if(intent != null)
			return intent.hasExtra(key) ? intent.getLongExtra(key, 0) : defaultValue;
		else
			return defaultValue;
	}
	/**
	 * Set value to intent or bundle
	 * @param key key
	 * @param value long value
	 */
	protected void setArg(String key, Boolean value){
		if(bundle != null)
			bundle.putBoolean(key, value);
		else if(intent != null)
			intent.putExtra(key, value);
	}

	/**
	 * Get value from intent or bundle
	 * @param key key
	 * @param defaultValue default value(nullable)
	 */
	protected Boolean getArg(String key, Boolean defaultValue){
		if(bundle != null)
			return bundle.containsKey(key) ? bundle.getBoolean(key) : defaultValue;
		else if(intent != null)
			return intent.hasExtra(key) ? intent.getBooleanExtra(key, false) : defaultValue;
		else
			return defaultValue;
	}
	/**
	 * Set value to intent or bundle
	 * @param key key
	 * @param value long value
	 */
	protected void setArg(String key, String value){
		if(bundle != null)
			bundle.putString(key, value);
		else if(intent != null)
			intent.putExtra(key, value);
	}

	/**
	 * Get value from intent or bundle
	 * @param key key
	 * @param defaultValue default value(nullable)
	 */
	protected String getArg(String key, String defaultValue){
		if(bundle != null)
			return bundle.containsKey(key) ? bundle.getString(key) : defaultValue;
		else if(intent != null)
			return intent.hasExtra(key) ? intent.getStringExtra(key) : defaultValue;
		else
			return defaultValue;
	}
}
