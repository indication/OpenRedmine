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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.ViewIssueList;

public class Issue extends ContentProvider {
	protected OrmLiteSqliteOpenHelper helper;
	protected RuntimeExceptionDao<ViewIssueList, Long> dao;
	protected RuntimeExceptionDao<RedmineFilter, Long> daoFilter;
	private static final String TAG = Issue.class.getSimpleName();
	protected static final String PROVIDER = BuildConfig.APPLICATION_ID + "." + TAG.toLowerCase(Locale.getDefault());
	public static final String PROVIDER_BASE = ContentResolver.SCHEME_CONTENT + "://" + PROVIDER;


	private enum IssueUrl {
		none,
		id,
		connection,
		project,
		project_detail,
		filter,
		filter_detail,
		category,
		version,
		status,
		;
		public static IssueUrl getEnum(int value){
			return IssueUrl.values()[value];
		}
	}
	private static final UriMatcher sURIMatcher = new UriMatcher(IssueUrl.none.ordinal());

	static {
		sURIMatcher.addURI(PROVIDER, "connection/#", IssueUrl.connection.ordinal());
		sURIMatcher.addURI(PROVIDER, "project/#", IssueUrl.project.ordinal());
		sURIMatcher.addURI(PROVIDER, "project_detail/#", IssueUrl.project_detail.ordinal());
		sURIMatcher.addURI(PROVIDER, "id/#", IssueUrl.id.ordinal());
		sURIMatcher.addURI(PROVIDER, "filter/#", IssueUrl.filter.ordinal());
		sURIMatcher.addURI(PROVIDER, "filter_detail/#", IssueUrl.filter_detail.ordinal());
		sURIMatcher.addURI(PROVIDER, "category/#", IssueUrl.category.ordinal());
		sURIMatcher.addURI(PROVIDER, "version/#", IssueUrl.version.ordinal());
		sURIMatcher.addURI(PROVIDER, "status/#", IssueUrl.status.ordinal());
		sURIMatcher.addURI(PROVIDER, "/", IssueUrl.none.ordinal());
	}

	@Override
	public boolean onCreate() {
		helper = new DatabaseCacheHelper(getContext());
		dao = helper.getRuntimeExceptionDao(ViewIssueList.class);
		daoFilter = helper.getRuntimeExceptionDao(RedmineFilter.class);
		// setup dao
		StringBuilder sb = new StringBuilder();
		ViewIssueList.createTempViewStatement(sb);
		try {
			helper.getConnectionSource().getReadWriteConnection().executeStatement(sb.toString(), -1);
		} catch (SQLException e) {
			Log.e(TAG, "onCreate", e);
		}
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
			QueryBuilder<ViewIssueList, Long> builder = dao.queryBuilder();
			Where<ViewIssueList, Long> where = builder.where();
			List<ArgumentHolder> args = new ArrayList<>();
			IssueUrl idtype = IssueUrl.none;

			if (selectionArgs != null)
				for (String item : selectionArgs){
					args.add(new SelectArg(SqlType.STRING, item));
				}
			if(sURIMatcher.match(uri) != -1)
				idtype = IssueUrl.getEnum(sURIMatcher.match(uri));
			switch(idtype){
				case id:
					where.eq("id", ContentUris.parseId(uri));
					break;
				case none:

					break;
				case project:
					RedmineFilter default_filter = daoFilter.queryForFirst(daoFilter.queryBuilder().where()
								.eq(RedmineFilter.PROJECT, ContentUris.parseId(uri))
								.and()
								.eq(RedmineFilter.CURRENT, true)
								.prepare()
						);
					if(default_filter != null) {
						setupWhere(default_filter, where);
						if(TextUtils.isEmpty(default_filter.getSort())){
							builder.orderBy(RedmineIssue.ISSUE_ID, false);
						} else {
							for(RedmineFilterSortItem key : default_filter.getSortList()){
								builder.orderBy(key.getDbKey(),key.isAscending());
							}
						}
					}
					break;
				case project_detail:
					RedmineFilter project_filter = daoFilter.queryForFirst(daoFilter.queryBuilder().where()
									.eq(RedmineFilter.PROJECT, ContentUris.parseId(uri))
									.and()
									.eq(RedmineFilter.CURRENT, true)
									.prepare()
					);
					if(project_filter != null) {
						MatrixCursor matrixCursor = new MatrixCursor(new String[] {"_id", "res_id", "res_name", "name"});
						addCursorRow(matrixCursor, project_filter);
						return matrixCursor;
					}
					break;
				case filter:
					RedmineFilter filter = daoFilter.queryForId(ContentUris.parseId(uri));
					if(filter == null) {
						where.eq("0","1");	//return empty
						break;
					}
					setupWhere(filter, where);
					if(TextUtils.isEmpty(filter.getSort())){
						builder.orderBy(RedmineIssue.ISSUE_ID, false);
					} else {
						for(RedmineFilterSortItem key : filter.getSortList()){
							builder.orderBy(key.getDbKey(),key.isAscending());
						}
					}
					break;
				case filter_detail:
					RedmineFilter filter_detail = daoFilter.queryForId(ContentUris.parseId(uri));
					MatrixCursor matrixCursor = new MatrixCursor(new String[] {"_id", "res_id", "res_name", "name"});
					addCursorRow(matrixCursor, filter_detail);
					return matrixCursor;
				case category:
					where.eq(RedmineIssue.CATEGORY, ContentUris.parseId(uri));
					builder.orderBy(RedmineIssue.MODIFIED, false);
					break;
				case version:
					where.eq(RedmineIssue.VERSION, ContentUris.parseId(uri));
					builder.orderBy(RedmineIssue.MODIFIED, false);
					break;
				case status:
					where.eq(RedmineIssue.STATUS, ContentUris.parseId(uri));
					builder.orderBy(RedmineIssue.MODIFIED, false);
					break;
				case connection:
					where.eq(RedmineIssue.CONNECTION, ContentUris.parseId(uri));
					builder.orderBy(RedmineIssue.MODIFIED, false);
					break;
				default:
					Log.e(TAG, "Not found:" + uri.toString());
					return null;
			}

			if(!StringUtils.isEmpty(selection)) {
				where.raw(selection, args.toArray(new ArgumentHolder[]{}));
			}
			if(!StringUtils.isEmpty(sortOrder))
				builder.orderByRaw(sortOrder);
			result = (AndroidDatabaseResults)dao.iterator(builder.prepare()).getRawResults();
		} catch (SQLException e) {
			Log.e(TAG, "query", e);
			return null;
		}
		Cursor cursor = result.getRawCursor();
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return new CursorWrapperNonId(cursor,RedmineProject.ID);
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
		cursor.addRow(new Object[] {id, title_id, null, changes.getName()});
	}
	protected void setupWhere(RedmineFilter filter,
							  Where<ViewIssueList, Long> where) throws SQLException {
		Hashtable<String, Object> dic = new Hashtable<>();
		if(filter.getConnectionId() != null) dic.put(RedmineFilter.CONNECTION,	filter.getConnectionId()		);
		if(filter.getProject()	 != null) dic.put(RedmineFilter.PROJECT,		filter.getProject().getId()		);
		if(filter.getTracker()	 != null) dic.put(RedmineFilter.TRACKER,		filter.getTracker().getId()		);
		if(filter.getAssigned()	 != null) dic.put(RedmineFilter.ASSIGNED,		filter.getAssigned().getId()	);
		if(filter.getAuthor()	 != null) dic.put(RedmineFilter.AUTHOR,			filter.getAuthor().getId()		);
		if(filter.getCategory()	 != null) dic.put(RedmineFilter.CATEGORY,		filter.getCategory().getId()	);
		if(filter.getStatus()	 != null) dic.put(RedmineFilter.STATUS,			filter.getStatus().getId()		);
		if(filter.getVersion()	 != null) dic.put(RedmineFilter.VERSION,		filter.getVersion().getId()		);
		if(filter.getPriority()	 != null) dic.put(RedmineFilter.PRIORITY,		filter.getPriority().getId()	);

		boolean isFirst = true;
		for(Enumeration<String> e = dic.keys() ; e.hasMoreElements() ;){
			String key = e.nextElement();
			if(dic.get(key) == null)
				continue;
			if(isFirst){
				isFirst = false;
			} else {
				where.and();
			}
			where.eq(key, dic.get(key));
		}
		// return no data
		if(dic.size() < 1){
			where.eq(RedmineFilter.CONNECTION, -1);
		}
	}

	@Override
	public String getType(Uri uri) {
		IssueUrl idtype = IssueUrl.none;
		if(sURIMatcher.match(uri) != -1)
			idtype = IssueUrl.getEnum(sURIMatcher.match(uri));
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
