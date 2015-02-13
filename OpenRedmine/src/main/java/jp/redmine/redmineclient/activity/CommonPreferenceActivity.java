package jp.redmine.redmineclient.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;

public class CommonPreferenceActivity extends PreferenceActivity {
	public CommonPreferenceActivity(){
		super();
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActivityHelper.setupTheme(this);
		super.onCreate(savedInstanceState);
		//getSupportActionBar();
		addPreferencesFromResource(R.xml.preference);
	}
}
