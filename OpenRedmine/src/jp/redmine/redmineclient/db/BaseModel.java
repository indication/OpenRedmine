package jp.redmine.redmineclient.db;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import android.content.Context;

public class BaseModel  <H extends OrmLiteSqliteOpenHelper>{
	private volatile H helper;

	@SuppressWarnings("unchecked")
	public BaseModel(Context context) {
		helper = (H) new DatabaseHelper(context);
	}

	public H getHelper() {
		return helper;
	}

}
