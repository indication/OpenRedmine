package jp.redmine.redmineclient.fragment.pager;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.param.ProjectArgument;

public class ConnectionHome extends Fragment {
	private static final String TAG = ConnectionHome.class.getSimpleName();

	public ConnectionHome() {
		super();
	}

	static public ConnectionHome newInstance(ConnectionArgument intent) {
		ConnectionHome instance = new ConnectionHome();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ProjectArgument arg = new ProjectArgument();
		arg.setArgument(getArguments());
		ViewPager viewPager = (ViewPager) getView().findViewById(R.id.pager);
		viewPager.setAdapter(new ConnectionPager(getChildFragmentManager(), arg));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.pager_main, container, false);
	}

	/*
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ActionBar actionBar = getActivity().getActionBar();
		if (actionBar == null)
			return;
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	}
	*/
}
