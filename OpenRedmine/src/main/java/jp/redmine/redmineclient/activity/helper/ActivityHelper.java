package jp.redmine.redmineclient.activity.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import jp.redmine.redmineclient.R;


public class ActivityHelper {
	static public final int ERROR_APP = 600;
	@SuppressLint("InlinedApi")
	static public void setupTheme(Activity activity){

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
		String setting = sp.getString("appearance_themes", "default");
		Integer theme = null;
		if("dark".equals(setting)){
			theme = R.style.ThemeDark;
		} else if("light".equals(setting)){
			theme = R.style.ThemeLight;
		} else if("hololight".equals(setting)){
			theme = R.style.ThemeLight;
		} else {
			theme = R.style.ThemeDefault;
		}
		if(theme != null){
			activity.setTheme(theme);
		}
	}

	static public void toastRemoteError(Context context, int statuscode){

		Integer resid = null;
		switch(statuscode){
		case 401:
			resid = R.string.remote_401;
			break;
		case 403:
			resid = R.string.remote_403;
			break;
		case 404:
			resid = R.string.remote_404;
			break;
		case 407:
			resid = R.string.remote_407;
			break;
		case 402:
		case 405:
			resid = R.string.remote_4xx;
			break;
		case 500:
		case 501:
		case 503:
			resid = R.string.remote_5xx;
			break;
		case ERROR_APP: //program error
			resid = R.string.remote_600;
			break;
		}
		if(resid != null){
			Toast.makeText(context,resid, Toast.LENGTH_SHORT).show();
		}
	}

}
