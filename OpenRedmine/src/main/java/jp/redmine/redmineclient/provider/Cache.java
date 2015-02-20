package jp.redmine.redmineclient.provider;

import com.tojc.ormlite.android.OrmLiteSimpleContentProvider;
import com.tojc.ormlite.android.framework.MatcherController;
import com.tojc.ormlite.android.framework.MimeTypeVnd;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectContract;

public class Cache extends OrmLiteSimpleContentProvider<DatabaseCacheHelper> {

	@Override
	protected Class<DatabaseCacheHelper> getHelperClass() {
		return DatabaseCacheHelper.class;
	}

	@Override
	public boolean onCreate() {
		setMatcherController(new MatcherController()
						.add(RedmineProject.class, MimeTypeVnd.SubType.DIRECTORY, "", RedmineProjectContract.CONTENT_URI_PATTERN_MANY)
						.add(RedmineProject.class, MimeTypeVnd.SubType.ITEM, "#", RedmineProjectContract.CONTENT_URI_PATTERN_ONE)
		);
		return true;
	}
}
