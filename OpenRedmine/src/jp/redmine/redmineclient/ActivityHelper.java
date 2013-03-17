package jp.redmine.redmineclient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Window;


public class ActivityHelper {
	static void setupTheme(Activity activity){

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
		String setting = sp.getString("appearance_themes", "default");
		Integer theme = null;
		if("dark".equals(setting)){
			theme = R.style.ThemeBlack;
		} else if("light".equals(setting)){
			theme = R.style.ThemeLight;
		} else if("hololight".equals(setting)){
			theme = R.style.ThemeHoloLight;
		}
		if(theme != null){
			activity.setTheme(theme);
			activity.getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		}
	}

}
