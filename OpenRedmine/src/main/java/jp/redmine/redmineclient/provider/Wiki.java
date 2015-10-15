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
import android.support.v4.database.CursorWrapperNonId;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineWiki;

public class Wiki extends ContentProvider {
	protected OrmLiteSqliteOpenHelper helper;
	protected RuntimeExceptionDao<RedmineWiki, Long> dao;
	private static final String TAG = Wiki.class.getSimpleName();
	protected static final String PROVIDER = BuildConfig.APPLICATION_ID + "." + TAG.toLowerCase(Locale.getDefault());
	public static final String PROVIDER_BASE = ContentResolver.SCHEME_CONTENT + "://" + PROVIDER;


	private enum WikiUrl {
		none,
		id,
		connection,
		project,
		page,
		;
		public static WikiUrl getEnum(int value){
			return WikiUrl.values()[value];
		}
	}
	private static final UriMatcher sURIMatcher = new UriMatcher(WikiUrl.none.ordinal());

	static {
		sURIMatcher.addURI(PROVIDER, "page/#", WikiUrl.page.ordinal());
		sURIMatcher.addURI(PROVIDER, "project/#", WikiUrl.project.ordinal());
		sURIMatcher.addURI(PROVIDER, "connection/#", WikiUrl.connection.ordinal());
		sURIMatcher.addURI(PROVIDER, "id/#", WikiUrl.id.ordinal());
		sURIMatcher.addURI(PROVIDER, "/", WikiUrl.none.ordinal());
	}

	@Override
	public boolean onCreate() {
		helper = new DatabaseCacheHelper(getContext());
		dao = helper.getRuntimeExceptionDao(RedmineWiki.class);
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

		AndroidDatabaseResults result;
		try {
			QueryBuilder<RedmineWiki, Long> builder = dao.queryBuilder();
			Where<RedmineWiki, Long> where = builder.where();
			List<ArgumentHolder> args = new ArrayList<>();
			WikiUrl idtype = WikiUrl.none;

			if (selectionArgs != null)
				for (String item : selectionArgs){
					args.add(new SelectArg(SqlType.UNKNOWN, item));
				}
			if(sURIMatcher.match(uri) != -1)
				idtype = WikiUrl.getEnum(sURIMatcher.match(uri));
			switch(idtype){
				case id:
					where.eq("id", ContentUris.parseId(uri));
					break;
				case none:

					break;
				case connection:
					where.eq(RedmineWiki.CONNECTION, ContentUris.parseId(uri));
					break;
				case project:
					where.eq(RedmineWiki.PROJECT_ID, ContentUris.parseId(uri));

					break;
				default:
					Log.e(TAG, "Not found:" + uri.toString());
					return null;
			}
			if(!StringUtils.isEmpty(selection))
				where.raw(selection, (ArgumentHolder[]) args.toArray());
			if(!StringUtils.isEmpty(sortOrder))
				builder.orderByRaw(sortOrder);
			result = (AndroidDatabaseResults)dao.iterator(builder.prepare()).getRawResults();
		} catch (SQLException e) {
			Log.e(TAG, "query", e);
			return null;
		}
		Cursor cursor = result.getRawCursor();
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return new CursorWrapperNonId(cursor,RedmineWiki.ID);
	}

	@Override
	public String getType(Uri uri) {
		WikiUrl idtype = WikiUrl.none;
		if(sURIMatcher.match(uri) != -1)
			idtype = WikiUrl.getEnum(sURIMatcher.match(uri));
		return idtype.name();
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
		try {
			UpdateBuilder<RedmineWiki, Long> builder = dao.updateBuilder();
			Where<RedmineWiki, Long> where = builder.where();
			List<ArgumentHolder> args = new ArrayList<>();
			WikiUrl idtype = WikiUrl.none;

			if (selectionArgs != null)
				for (String item : selectionArgs){
					args.add(new SelectArg(SqlType.UNKNOWN, item));
				}
			if(sURIMatcher.match(uri) != -1)
				idtype = WikiUrl.getEnum(sURIMatcher.match(uri));
			switch(idtype){
				case id:
					where.eq("id", ContentUris.parseId(uri));
					break;
				case none:

					break;
				default:
					Log.e(TAG, "Not found:" + uri.toString());
					return 0;
			}
			if(!StringUtils.isEmpty(selection))
				where.raw(selection, (ArgumentHolder[]) args.toArray());

			for(Map.Entry<String, Object> item : values.valueSet()){
				builder.updateColumnValue(item.getKey(), item.getValue());
			}
			return builder.update();
		} catch (SQLException e) {
			Log.e(TAG, "query", e);
			return 0;
		}

	}

}
