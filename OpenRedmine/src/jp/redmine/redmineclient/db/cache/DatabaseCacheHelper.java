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
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			//自動生成
			TableUtils.createTable(arg1, RedmineProject.class);
			TableUtils.createTable(arg1, RedmineUser.class);
			TableUtils.createTable(arg1, RedmineProjectCategory.class);
			TableUtils.createTable(arg1, RedmineProjectVersion.class);
			TableUtils.createTable(arg1, RedminePriority.class);
			TableUtils.createTable(arg1, RedmineRole.class);
			TableUtils.createTable(arg1, RedmineStatus.class);
			TableUtils.createTable(arg1, RedmineTracker.class);
			TableUtils.createTable(arg1, RedmineIssue.class);
			TableUtils.createTable(arg1, RedmineProjectMember.class);
			TableUtils.createTable(arg1, RedmineFilter.class);
			TableUtils.createTable(arg1, RedmineJournal.class);

		} catch (SQLException e) {
			Log.e("DatabaseHelper","onCreate",e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int older,int newer) {
		try {
			switch(older){
			case 1:
				TableUtils.createTable(arg1, RedmineProjectMember.class);
				TableUtils.createTable(arg1, RedmineFilter.class);
				break;
			case 2:
				TableUtils.createTable(arg1, RedmineJournal.class);
				//TODO issue.id int->long
				break;
			}

		} catch (SQLException e) {
			Log.e("DatabaseHelper","onCreate",e);
		}


	}

}
