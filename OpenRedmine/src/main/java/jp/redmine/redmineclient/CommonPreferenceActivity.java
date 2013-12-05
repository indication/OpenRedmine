package jp.redmine.redmineclient;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class CommonPreferenceActivity extends SherlockPreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
	}
}
