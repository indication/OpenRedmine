package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;

import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectMember;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineRole;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseCacheHelper extends OrmLiteSqliteOpenHelper {
	private static String DB_NAME="OpenRedmineCache.db";
	private static int DB_VERSION=3;

    public DatabaseCacheHelper(Context context) {
    	super(context, getDatabasePath(context), null, DB_VERSION);
    }

    public static String getDatabasePath(Context context){
    	return context.getCacheDir().getPath() + "/" +DB_NAME;
    }
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource source) {
		try {
			//自動生成
			TableUtils.createTable(source, RedmineProject.class);
			TableUtils.createTable(source, RedmineUser.class);
			TableUtils.createTable(source, RedmineProjectCategory.class);
			TableUtils.createTable(source, RedmineProjectVersion.class);
			TableUtils.createTable(source, RedminePriority.class);
			TableUtils.createTable(source, RedmineRole.class);
			TableUtils.createTable(source, RedmineStatus.class);
			TableUtils.createTable(source, RedmineTracker.class);
			TableUtils.createTable(source, RedmineIssue.class);
			TableUtils.createTable(source, RedmineProjectMember.class);
			TableUtils.createTable(source, RedmineFilter.class);
			TableUtils.createTable(source, RedmineJournal.class);

		} catch (SQLException e) {
			Log.e("DatabaseHelper","onCreate",e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource source, int older,int newer) {
		try {
			switch(older){
			case 1:
				TableUtils.createTable(source, RedmineProjectMember.class);
				TableUtils.createTable(source, RedmineFilter.class);
			case 2:
				TableUtils.dropTable(source, RedmineIssue.class,true);
				TableUtils.createTable(source, RedmineJournal.class);
				TableUtils.createTable(source, RedmineIssue.class);
			case 3:
				addColumn(db,RedmineProjectVersion.class,"sharing TEXT");
				addColumn(db,RedmineProjectVersion.class,"description TEXT");
				break;
			}

		} catch (SQLException e) {
			Log.e("DatabaseHelper","onCreate",e);
		}


	}


	protected void addColumn(SQLiteDatabase db, Class<?> name, String column){
		db.execSQL("ALTER TABLE "
				+ name.getName()
				+ " ADD COLUMN "
				+ column
				+ ";");

	}

}
