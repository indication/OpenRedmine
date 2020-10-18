package jp.redmine.redmineclient.activity;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.pager.CorePage;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.fragment.AboutFragment;
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

		List<CorePage> list = new ArrayList<>();

		list.add((new PageInformation()).setParam(null)
						.setName(getString(R.string.information))
						.setIcon(R.drawable.ic_info)
		);

		ResourceArgument intent;
		intent = new ResourceArgument();
		intent.setArgument();
		intent.setResource(R.raw.lisence);
		list.add((new PageMarkdown()).setParam(intent)
				.setName(getString(R.string.license))
				.setIcon(R.drawable.ic_book)
		);

		intent = new ResourceArgument();
		intent.setArgument();
		intent.setResource(R.raw.contributors);
		list.add((new PageMarkdown()).setParam(intent)
				.setName(getString(R.string.contributors))
				.setIcon(R.drawable.ic_people)
		);

		intent = new ResourceArgument();
		intent.setArgument();
		intent.setResource(R.raw.version);
		list.add((new PageMarkdown()).setParam(intent)
						.setName(getString(R.string.ticket_version))
						.setIcon(R.drawable.ic_history)
		);

		intent = new ResourceArgument();
		intent.setArgument();
		intent.setResource(R.raw.store);
		list.add((new PageMarkdown()).setParam(intent)
						.setName(getString(R.string.product))
						.setIcon(R.drawable.ic_project)
		);

		intent = new ResourceArgument();
		intent.setArgument();
		intent.setResource(R.raw.privacy_policy);
		list.add((new PageMarkdown()).setParam(intent)
				.setName(getString(R.string.privacy_policy))
				.setIcon(R.drawable.ic_project)
		);
		intent = new ResourceArgument();
		intent.setArgument();
		intent.setResource(R.raw.terms_and_conditions);
		list.add((new PageMarkdown()).setParam(intent)
				.setName(getString(R.string.terms_and_conditions))
				.setIcon(R.drawable.ic_project)
		);
		return list;
	}

	private class PageInformation extends CorePage<Void> {
		@Override
		public Fragment getRawFragment(Void param) {
			return new AboutFragment();
		}
	}

	private class PageMarkdown extends CorePage<ResourceArgument> {
		@Override
		public Fragment getRawFragment(ResourceArgument param) {
			return ResourceMarkdown.newInstance(param);
		}
	}
}
