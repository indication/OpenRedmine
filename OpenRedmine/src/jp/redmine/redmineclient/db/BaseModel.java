package jp.redmine.redmineclient.db;

import jp.redmine.redmineclient.DatabaseHelper;
import android.content.Context;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public class BaseModel <H extends OrmLiteSqliteOpenHelper>{
	private volatile H helper;

	@SuppressWarnings("unchecked")
	public BaseModel(Context context) {
		//helper = (H) OpenHelperManager.getHelper(context);
		helper = (H) new DatabaseHelper(context);
	}

	public H getHelper() {
		return helper;
	}

}
