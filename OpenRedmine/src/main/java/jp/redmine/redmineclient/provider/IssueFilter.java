package jp.redmine.redmineclient.provider;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.database.CursorWrapperNonId;
import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;

public class IssueFilter extends ContentProvider {
	protected OrmLiteSqliteOpenHelper helper;
	protected RuntimeExceptionDao<RedmineFilter, Long> daoFilter;
	protected RuntimeExceptionDao<RedmineProjectCategory, Long> daoCategory;
	protected RuntimeExceptionDao<RedmineProject, Long> daoProject;
	protected RuntimeExceptionDao<RedmineProjectVersion, Long> daoVersion;
	private static final String TAG = IssueFilter.class.getSimpleName();
	protected static final String PROVIDER = BuildConfig.APPLICATION_ID + "." + TAG.toLowerCase(Locale.getDefault());
	public static final String PROVIDER_BASE = ContentResolver.SCHEME_CONTENT + "://" + PROVIDER;


	private static final UriMatcher sURIMatcher = new UriMatcher(Issue.IssueUrl.none.ordinal());

	static {
		sURIMatcher.addURI(PROVIDER, "connection/#", Issue.IssueUrl.connection.ordinal());
		sURIMatcher.addURI(PROVIDER, "project/#", Issue.IssueUrl.project.ordinal());
		sURIMatcher.addURI(PROVIDER, "id/#", Issue.IssueUrl.id.ordinal());
		sURIMatcher.addURI(PROVIDER, "filter/#", Issue.IssueUrl.filter.ordinal());
		sURIMatcher.addURI(PROVIDER, "category/#", Issue.IssueUrl.category.ordinal());
		sURIMatcher.addURI(PROVIDER, "version/#", Issue.IssueUrl.version.ordinal());
		sURIMatcher.addURI(PROVIDER, "status/#", Issue.IssueUrl.status.ordinal());
		sURIMatcher.addURI(PROVIDER, "/", Issue.IssueUrl.none.ordinal());
	}

	@Override
	public boolean onCreate() {
		helper = new DatabaseCacheHelper(getContext());
		daoFilter = helper.getRuntimeExceptionDao(RedmineFilter.class);
		daoCategory = helper.getRuntimeExceptionDao(RedmineProjectCategory.class);
		daoProject = helper.getRuntimeExceptionDao(RedmineProject.class);
		daoVersion = helper.getRuntimeExceptionDao(RedmineProjectVersion.class);
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

		MatrixCursor matrixCursor = new MatrixCursor(new String[] {"_id", "res_id", "res_name", "name"});
		AndroidDatabaseResults result;
		Issue.IssueUrl idtype = Issue.IssueUrl.none;
		try {
			if(sURIMatcher.match(uri) != -1)
				idtype = Issue.IssueUrl.getEnum(sURIMatcher.match(uri));
			switch(idtype){
				case id:
					break;
				case project:
					RedmineFilter project_filter = daoFilter.queryForFirst(daoFilter.queryBuilder().where()
									.eq(RedmineFilter.PROJECT, ContentUris.parseId(uri))
									.and()
									.eq(RedmineFilter.CURRENT, true)
									.prepare()
					);

					if(project_filter == null) {
						RedmineProject proj = daoProject.queryForId(ContentUris.parseId(uri));
						addCursorRow(matrixCursor,
							(proj == null) ? String.valueOf(ContentUris.parseId(uri)) : proj.getName(),
							1, R.string.ticket_project);
					} else {
						addCursorRow(matrixCursor, project_filter);
					}
					break;
				case filter:
					RedmineFilter filter_detail = daoFilter.queryForId(ContentUris.parseId(uri));
					addCursorRow(matrixCursor, filter_detail);
					break;
				case category:
					RedmineProjectCategory category = daoCategory.queryForId(ContentUris.parseId(uri));
					if(category != null) {
						addCursorRow(matrixCursor, category.getProject().getName(), 1, R.string.ticket_project);
						addCursorRow(matrixCursor, category.getName(), 2, R.string.ticket_category);
					}
					break;
				case version:
					RedmineProjectVersion version = daoVersion.queryForId(ContentUris.parseId(uri));
					if(version != null) {
						addCursorRow(matrixCursor, version.getProject().getName(), 1, R.string.ticket_project);
						addCursorRow(matrixCursor, version.getName(), 2, R.string.ticket_version);
					}
					break;
				case none:

					break;
				default:
					Log.e(TAG, "Not found:" + uri.toString());
					return null;
			}
		} catch (SQLException e) {
			Log.e(TAG, "query", e);
			return null;
		}
		return matrixCursor;
	}
	protected void addCursorRow(MatrixCursor cursor, RedmineFilter item) {
		int counter = 0;
		if(item == null)
			return;

		addCursorRow(cursor, item.getProject(), counter++, R.string.ticket_project);
		addCursorRow(cursor, item.getCategory(), counter++, R.string.ticket_category);
		addCursorRow(cursor, item.getAuthor(), counter++, R.string.ticket_author);
		addCursorRow(cursor, item.getAssigned(), counter++, R.string.ticket_assigned);
		addCursorRow(cursor, item.getPriority(), counter++, R.string.ticket_priority);
		addCursorRow(cursor, item.getStatus(), counter++, R.string.ticket_status);
		addCursorRow(cursor, item.getTracker(), counter++, R.string.ticket_tracker);
		addCursorRow(cursor, item.getVersion(), counter++, R.string.ticket_version);

		if (!TextUtils.isEmpty(item.getSort())) {
			RedmineFilterSortItem sort = RedmineFilterSortItem.setupFilter(new RedmineFilterSortItem(), item.getSort());
			if(sort.getResource() != 0) {
				addCursorRow(cursor, item.getVersion(), counter++, sort.getResource());
				cursor.addRow(new Object[]{counter++, R.string.ticket_sort, sort.getResource(),""});
			}
		}

	}
	protected void addCursorRow(MatrixCursor cursor, IMasterRecord changes, int id, int title_id) {
		if(changes == null)
			return;
		addCursorRow(cursor, changes.getName(), id, title_id);
	}

	protected void addCursorRow(MatrixCursor cursor, String changes, int id, int title_id) {
		if(changes == null)
			return;
		cursor.addRow(new Object[]{id, title_id, null, changes});
	}

	@Override
	public String getType(Uri uri) {
		Issue.IssueUrl idtype = Issue.IssueUrl.none;
		if(sURIMatcher.match(uri) != -1)
			idtype = Issue.IssueUrl.getEnum(sURIMatcher.match(uri));
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
		return 0;
	}

}
