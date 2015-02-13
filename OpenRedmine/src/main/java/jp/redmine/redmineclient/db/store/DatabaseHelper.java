package jp.redmine.redmineclient.db.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableInfo;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
					HashMap<String,String> replace = new HashMap<>();
					replace.put("_id","id");
					renameColumn(db, source, RedmineConnection.class, replace);
					break;
			}
		} catch (SQLException e) {
			Log.e("DatabaseHelper","onCreate",e);
		}


	}

	protected void renameColumn(SQLiteDatabase db, ConnectionSource source, Class<?> name, HashMap<String,String> replace) throws SQLException {
		List<String> fieldsNew = getFields(source, name);
		List<String> fieldsOld = new ArrayList<>();
		for(String field : fieldsNew){
			fieldsOld.add(replace.containsKey(field) ? replace.get(field) : field);
		}

		String worktable = name.getSimpleName() + "wk";
		db.execSQL("ALTER TABLE "
			+ name.getSimpleName()
			+ " RENAME TO "
			+ worktable
			+ ";");
		TableUtils.createTable(source, name);
		db.execSQL("INSERT INTO "
				+ name.getSimpleName()
				+ " ("
				+ TextUtils.join(",",fieldsNew)
				+ " ) SELECT "
				+ TextUtils.join(",",fieldsOld)
				+ " FROM  "
				+ worktable
				+ ";");
		db.execSQL("DROP TABLE "
			+ worktable
			+ ";");

	}

	protected <T,ID> List<String> getFields(ConnectionSource source, Class<T> name) throws SQLException {
		Dao<T, ID> dao = DaoManager.createDao(source, name);
		List<String> fields = new ArrayList<>();
		FieldType[] fieldTypes;
		if (dao instanceof BaseDaoImpl<?, ?>) {
			fieldTypes =  ((BaseDaoImpl<?, ?>) dao).getTableInfo().getFieldTypes();
		} else {
			fieldTypes =  (new TableInfo<T, ID>(source, null, name)).getFieldTypes();
		}
		for( FieldType field :fieldTypes){
			fields.add(field.getColumnName());
		}
		return fields;
	}
}
