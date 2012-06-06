package jp.redmine.redmineclient.db;

import android.content.Context;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public class BaseCacheModel <H extends OrmLiteSqliteOpenHelper>{
	private volatile H helper;

	@SuppressWarnings("unchecked")
	public BaseCacheModel(Context context) {
		helper = (H) new DatabaseCacheHelper(context);
	}

	public H getHelper() {
		return helper;
	}

}
