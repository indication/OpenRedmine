package jp.redmine.redmineclient.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.R;

public class AboutFragment extends Fragment {

	public AboutFragment() {}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_splash, container, false);
		TextView text = (TextView)view.findViewById(R.id.footer);
		if (text != null)
			text.setText(getString(R.string.footer_version,
					BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME,
					BuildConfig.VERSION_CODE, BuildConfig.BUILD_ENV
			));
		return view;
	}
}
