package jp.redmine.redmineclient.provider;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.Locale;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.db.store.DatabaseHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;

public class Connection extends ContentProvider {
	protected OrmLiteSqliteOpenHelper helper;
	protected RuntimeExceptionDao<RedmineConnection, Integer> dao;
	private static final String TAG = Connection.class.getSimpleName();
	protected static final String PROVIDER = BuildConfig.APPLICATION_ID + "." + TAG.toLowerCase(Locale.getDefault());
	protected static final String PROVIDER_BASE = ContentResolver.SCHEME_CONTENT + "://" + PROVIDER;


	private enum ConnectionUrl {
		none,
		id,
		;
		public static ConnectionUrl getEnum(int value){
			return ConnectionUrl.values()[value];
		}
	}
	private static final UriMatcher sURIMatcher = new UriMatcher(ConnectionUrl.none.ordinal());

	static {
		sURIMatcher.addURI(PROVIDER, "id/#", ConnectionUrl.id.ordinal());
		sURIMatcher.addURI(PROVIDER, "", ConnectionUrl.none.ordinal());
	}

	@Override
	public boolean onCreate() {
		helper = new DatabaseHelper(getContext());
		dao = helper.getRuntimeExceptionDao(RedmineConnection.class);
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void shutdown() {
		helper.close();
		helper = null;
		super.shutdown();
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		PreparedQuery<RedmineConnection> builder;
		try {
			switch(ConnectionUrl.getEnum(sURIMatcher.match(uri))){
				case id:
					builder = dao.queryBuilder().where()
							.eq(RedmineConnection.ID, ContentUris.parseId(uri))
							.prepare();
					break;
				case none:
					builder = dao.queryBuilder()
							.prepare();
					break;
				default:
					Log.e(TAG, "Not found:" + uri.toString());
					return null;
			}
		} catch (SQLException e) {
			Log.e(TAG, "query", e);
			return null;
		}
		AndroidDatabaseResults result = (AndroidDatabaseResults)dao.iterator(builder).getRawResults();
		Cursor cursor = result.getRawCursor();
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return ConnectionUrl.getEnum(sURIMatcher.match(uri)).name();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

}
