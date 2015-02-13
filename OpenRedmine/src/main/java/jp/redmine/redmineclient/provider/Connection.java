package jp.redmine.redmineclient.provider;

import com.tojc.ormlite.android.OrmLiteSimpleContentProvider;
import com.tojc.ormlite.android.framework.MatcherController;
import com.tojc.ormlite.android.framework.MimeTypeVnd;

import jp.redmine.redmineclient.db.store.DatabaseHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineConnectionContract;

public class Connection extends OrmLiteSimpleContentProvider<DatabaseHelper> {

	@Override
	protected Class<DatabaseHelper> getHelperClass() {
		return DatabaseHelper.class;
	}

	@Override
	public boolean onCreate() {
		setMatcherController(new MatcherController()
						.add(RedmineConnection.class, MimeTypeVnd.SubType.DIRECTORY, "", RedmineConnectionContract.CONTENT_URI_PATTERN_MANY)
						.add(RedmineConnection.class, MimeTypeVnd.SubType.ITEM, "#", RedmineConnectionContract.CONTENT_URI_PATTERN_ONE)
		);
		return true;
	}

	@Override
	protected DatabaseHelper createHelper() {
		return new DatabaseHelper(getContext());
	}

	@Override
	protected void releaseHelper() {
		//super.releaseHelper();
	}
}
