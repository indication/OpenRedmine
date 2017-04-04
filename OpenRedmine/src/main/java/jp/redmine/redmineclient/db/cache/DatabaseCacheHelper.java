package jp.redmine.redmineclient.db.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.sql.SQLException;

import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineAttachmentData;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineIssueRelation;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedmineNews;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectMember;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineRecentIssue;
import jp.redmine.redmineclient.entity.RedmineRole;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTimeActivity;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.RedmineWatcher;
import jp.redmine.redmineclient.entity.RedmineWiki;

public class DatabaseCacheHelper extends OrmLiteSqliteOpenHelper {
	private static String DB_NAME="OpenRedmineCache.db";
	private static int DB_VERSION=18;

    public DatabaseCacheHelper(Context context) {
    	super(context, getDatabasePath(context), null, DB_VERSION);
    }
    public static String getDatabasePath(Context context){
		File dir = context.getCacheDir();
		if (dir == null)
			dir = context.getExternalCacheDir();
		return dir.getPath() + "/" +DB_NAME;
    }
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource source) {
		try {
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
			TableUtils.createTable(source, RedmineTimeEntry.class);
			TableUtils.createTable(source, RedmineTimeActivity.class);
			TableUtils.createTable(source, RedmineIssueRelation.class);
			TableUtils.createTable(source, RedmineAttachment.class);
			TableUtils.createTable(source, RedmineAttachmentData.class);
			TableUtils.createTable(source, RedmineWiki.class);
			TableUtils.createTable(source, RedmineNews.class);
			TableUtils.createTable(source, RedmineRecentIssue.class);
			TableUtils.createTable(source, RedmineWatcher.class);

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
				addColumn(db,RedmineProject.class,"parent INTEGER");
				TableUtils.createTable(source, RedmineTimeEntry.class);
				TableUtils.createTable(source, RedmineTimeActivity.class);
			case 4:
				if(older > 2){
					addColumn(db,RedmineIssue.class,"closed TEXT");
				}
			case 5:
				addColumn(db,RedminePriority.class,"is_default BOOLEAN");
			case 6:
				addColumn(db,RedmineUser.class,"is_current BOOLEAN");
			case 7:
				TableUtils.createTable(source, RedmineIssueRelation.class);
			case 8:
				TableUtils.createTable(source, RedmineAttachment.class);
			case 9:
				addColumn(db,RedmineRole.class,"permissions TEXT");
			case 10:
				TableUtils.createTable(source, RedmineWiki.class);
			case 11:
				TableUtils.createTable(source, RedmineNews.class);
			case 12:
				addColumn(db,RedmineProject.class,"status INTEGER");
			case 13:
				TableUtils.createTable(source, RedmineAttachmentData.class);
			case 14:
				if(older > 8) {
					addColumn(db,RedmineAttachment.class,"wiki_id TEXT");
				}
			case 15:
				TableUtils.createTable(source, RedmineRecentIssue.class);
			case 16:
				TableUtils.createTable(source, RedmineWatcher.class);
			case 17:
				if(older > 1)
					addColumn(db, RedmineFilter.class,"is_closed INTEGER");
				break;
			}

		} catch (SQLException e) {
			Log.e("DatabaseHelper","onCreate",e);
		}


	}


	protected void addColumn(SQLiteDatabase db, Class<?> name, String column){
		db.execSQL("ALTER TABLE "
				+ name.getSimpleName()
				+ " ADD COLUMN "
				+ column
				+ ";");

	}

}
