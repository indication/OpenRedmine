package jp.redmine.redmineclient.db.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;

import jp.redmine.redmineclient.db.AppDatabaseUtils;
import jp.redmine.redmineclient.entity.RedmineConnection;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private static String DB_NAME="OpenRedmine.db";
	private static int DB_VERSION=2;

    public DatabaseHelper(Context context) {
    	super(context, DB_NAME, null, DB_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			TableUtils.createTable(arg1, RedmineConnection.class);

		} catch (SQLException e) {
			Log.e("DatabaseHelper","onCreate",e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource source, int older,int newer) {
		try {
			switch(older){
				case 1:
					HashMap<String,String> replace = new HashMap<String,String>();
					replace.put("_id","id");
					AppDatabaseUtils.renameColumn(db, source, RedmineConnection.class, replace);
					break;
			}
		} catch (SQLException e) {
			Log.e("DatabaseHelper","onCreate",e);
		}


	}

}
