package jp.redmine.redmineclient.db;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

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

public class AppDatabaseUtils {

	public static void addColumn(SQLiteDatabase db, Class<?> name, String column){
		db.execSQL("ALTER TABLE "
				+ name.getSimpleName()
				+ " ADD COLUMN "
				+ column
				+ ";");

	}
	public static void renameColumn(SQLiteDatabase db, ConnectionSource source, Class<?> name, HashMap<String,String> replace) throws SQLException {
		FieldType[] fields = getFields(source, name);
		List<String> fieldsNew = getFieldsName(fields);
		List<String> fieldsOld = new ArrayList<String>();
		for(String field : fieldsNew){
			fieldsOld.add(replace.containsKey(field) ? replace.get(field) : field);
		}
		for(String keyname : getFieldsKeys(fields)){
			db.execSQL("DROP INDEX " + keyname + ";");
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
				+ TextUtils.join(",", fieldsNew)
				+ " ) SELECT "
				+ TextUtils.join(",",fieldsOld)
				+ " FROM  "
				+ worktable
				+ ";");
		db.execSQL("DROP TABLE "
				+ worktable
				+ ";");

	}

	static <T,ID> FieldType[] getFields(ConnectionSource source, Class<T> name) throws SQLException {
		Dao<T, ID> dao = DaoManager.createDao(source, name);
		if (dao instanceof BaseDaoImpl<?, ?>) {
			return ((BaseDaoImpl<?, ?>) dao).getTableInfo().getFieldTypes();
		} else {
			return (new TableInfo<T, ID>(source, null, name)).getFieldTypes();
		}
	}
	static List<String> getFieldsName(FieldType[] fieldTypes) throws SQLException {
		List<String> fields = new ArrayList<String>();
		for( FieldType field : fieldTypes){
			fields.add(field.getColumnName());
		}
		return fields;
	}
	static List<String> getFieldsKeys(FieldType[] fieldTypes) throws SQLException {
		List<String> fields = new ArrayList<String>();
		for( FieldType field : fieldTypes){
			addListIfNotExistsAndNotEmpty(fields,field.getIndexName());
			addListIfNotExistsAndNotEmpty(fields,field.getUniqueIndexName());
		}
		return fields;
	}
	static void addListIfNotExistsAndNotEmpty(List<String> fields, String item){
		if(!TextUtils.isEmpty(item))
			addListIfNotExists(fields, item);
	}
	static <T> void addListIfNotExists(List<T> fields, T item){
		if(!fields.contains(item))
			fields.add(item);
	}
}
