package jp.redmine.redmineclient;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class CommonPreferenceActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
	}
}
