package jp.redmine.redmineclient;

import java.util.HashMap;
import java.util.Map.Entry;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.store.DatabaseHelper;

import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;


public class DbBaseActivity
	extends  OrmLiteBaseActivity<DatabaseHelper>  {
	public DbBaseActivity() {
		super();
	}
	private volatile HashMap<Class<?>, OrmLiteSqliteOpenHelper> handles;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handles = new HashMap<Class<?>, OrmLiteSqliteOpenHelper>();
	}

	protected DatabaseHelper getHelperStore(){
		return getHelper();
	}

	protected DatabaseCacheHelper getHelperCache(){
		return getHelperMulti(DatabaseCacheHelper.class);
	}

	@SuppressWarnings("unchecked")
	protected <T extends OrmLiteSqliteOpenHelper> T getHelperMulti(Class<T> cls){
		if (handles == null) {
			throw new IllegalStateException("Helper is null for some unknown reason");
		}
		OrmLiteSqliteOpenHelper helper;
		if(handles.containsKey(cls)){
			helper = handles.get(cls);
		} else {
			helper = OpenHelperManager.getHelper(this, cls);
			handles.put(cls,helper);
		}
		return (T)helper;
	}

	@Override
	protected void onDestroy() {
		for(Entry<Class<?>, OrmLiteSqliteOpenHelper> item :handles.entrySet()){
			OpenHelperManager.releaseHelper();
			handles.remove(item.getKey());
		}
		handles = null;
		super.onDestroy();
	}
}
