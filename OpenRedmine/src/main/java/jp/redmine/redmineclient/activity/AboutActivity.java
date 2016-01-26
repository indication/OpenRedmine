package jp.redmine.redmineclient.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.pager.CorePage;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.fragment.ActivityInterface;
import jp.redmine.redmineclient.fragment.ResourceMarkdown;
import jp.redmine.redmineclient.param.ResourceArgument;

public class AboutActivity extends TabActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	private static final String TAG = AboutActivity.class.getSimpleName();
	public AboutActivity(){
		super();
	}

	@Override
	protected List<CorePage> getTabs(){

		List<CorePage> list = new ArrayList<CorePage>();

		list.add((new PageInformation()).setParam(null)
						.setName(getString(R.string.information))
						.setIcon(android.R.drawable.ic_menu_gallery)
		);

		ResourceArgument intent;
		intent = new ResourceArgument();
		intent.setArgument();
		intent.setResource(R.raw.lisence);
		list.add((new PageMarkdown()).setParam(intent)
				.setName(getString(R.string.license))
				.setIcon(android.R.drawable.ic_menu_share)
		);

		intent = new ResourceArgument();
		intent.setArgument();
		intent.setResource(R.raw.contributors);
		list.add((new PageMarkdown()).setParam(intent)
				.setName(getString(R.string.contributors))
				.setIcon(android.R.drawable.ic_menu_compass)
		);

		intent = new ResourceArgument();
		intent.setArgument();
		intent.setResource(R.raw.version);
		list.add((new PageMarkdown()).setParam(intent)
						.setName(getString(R.string.ticket_version))
						.setIcon(android.R.drawable.ic_menu_recent_history)
		);

		intent = new ResourceArgument();
		intent.setArgument();
		intent.setResource(R.raw.store);
		list.add((new PageMarkdown()).setParam(intent)
						.setName(getString(R.string.product))
						.setIcon(android.R.drawable.ic_menu_slideshow)
		);
		return list;
	}

	private class PageInformation extends CorePage<Void> {
		@Override
		public Fragment getRawFragment(Void param) {
			return new Fragment(){

				@Override
				public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
					return inflater.inflate(R.layout.page_splash, container, false);
				}

				@Override
				public void onActivityCreated(Bundle savedInstanceState) {
					super.onActivityCreated(savedInstanceState);
					TextView view = (TextView)findViewById(R.id.footer);
					if (view != null)
						view.setText(getString(R.string.footer_version,
								BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME,
								BuildConfig.VERSION_CODE, BuildConfig.BUILD_TYPE
						));
				}
			};
		}
	}

	private class PageMarkdown extends CorePage<ResourceArgument> {
		@Override
		public Fragment getRawFragment(ResourceArgument param) {
			return ResourceMarkdown.newInstance(param);
		}
	}
}
