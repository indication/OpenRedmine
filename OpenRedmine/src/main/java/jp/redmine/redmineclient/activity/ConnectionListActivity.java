package jp.redmine.redmineclient.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
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
import jp.redmine.redmineclient.fragment.ConnectionList;
import jp.redmine.redmineclient.fragment.ProjectFavoriteList;
import jp.redmine.redmineclient.fragment.RecentIssueList;
import jp.redmine.redmineclient.fragment.ResourceMarkdown;
import jp.redmine.redmineclient.param.ResourceArgument;

public class ConnectionListActivity extends TabActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	public ConnectionListActivity(){
		super();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.title_home);
	}
	@Override
	protected List<CorePage> getTabs(){

		List<CorePage> list = new ArrayList<CorePage>();
		list.add((new CorePage<Void>() {
					@Override
					public Fragment getRawFragment(Void param) {
						return ConnectionList.newInstance();
					}
				})
						.setParam(null)
						.setName(getString(R.string.connection))
						.setIcon(R.drawable.ic_domain)
		);

		list.add((new CorePage<Void>() {
					@Override
					public Fragment getRawFragment(Void param) {
						return ProjectFavoriteList.newInstance();
					}
				})
				.setParam(null)
				.setName(getString(R.string.favorite))
				.setIcon(R.drawable.ic_project_favorite)
		);
		list.add((new CorePage<Void>() {
					@Override
					public Fragment getRawFragment(Void param) {
						return RecentIssueList.newInstance();
					}
				})
						.setParam(null)
						.setName(getString(R.string.recent_issues))
						.setIcon(R.drawable.ic_recent)
		);

		//from AboutActivity
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
