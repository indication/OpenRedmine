package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.adapter.form.IssueForm;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class IssueListAdapter extends RedmineDaoAdapter<RedmineIssue, Long, DatabaseCacheHelper> {
	private static final String TAG = IssueListAdapter.class.getSimpleName();
	private RedmineFilterModel mFilter;
	protected Integer connection_id;
	protected Long project_id;
	protected Integer filter_id;
	public IssueListAdapter(DatabaseCacheHelper helper, Context context) {
		super(helper, context, RedmineIssue.class);
		mFilter = new RedmineFilterModel(helper);
	}

	public void setupParameter(int connection, long project){
		connection_id = connection;
		project_id = project;
	}

	public void setupParameter(int filter){
		filter_id = filter;
	}

    @Override
	public boolean isValidParameter(){
		if(project_id != null && connection_id != null)
			return true;
		if(filter_id != null)
			return true;
		return false;
	}

	@Override
	protected int getItemViewId() {
		return R.layout.listitem_issue;
	}

	@Override
	protected void setupView(View view, RedmineIssue data) {
		IssueForm form;
		if(view.getTag() != null && view.getTag() instanceof IssueForm){
			form = (IssueForm)view.getTag();
		} else {
			form = new IssueForm(view);
		}
		form.setValue(data);
	}

	@Override
	protected long getDbItemId(RedmineIssue item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

	public RedmineFilter getParameter() {
		RedmineFilter filter = null;
		try {
			if(filter_id != null)
				filter = mFilter.fetchById(filter_id);
			else
				filter = mFilter.fetchByCurrent(connection_id, project_id);
		} catch (SQLException e) {
			Log.e(TAG, "getParameter", e);
		}
		if(filter == null)
			filter = new RedmineFilter();
		return filter;
	}

	@Override
	protected QueryBuilder<RedmineIssue, Long> getQueryBuilder() throws SQLException {
		RedmineFilter filter = getParameter();
		QueryBuilder<RedmineIssue, Long> builder = dao.queryBuilder();
		Where<RedmineIssue, Long> where = builder.where();
		setupWhere(filter,where);
		builder.setWhere(where);
		if(TextUtils.isEmpty(filter.getSort())){
			builder.orderBy(RedmineIssue.ISSUE_ID, false);
		} else {
			for(RedmineFilterSortItem key : filter.getSortList()){
				builder.orderBy(key.getDbKey(),key.isAscending());
			}
		}
		return builder;
	}
	@Override
	protected QueryBuilder<RedmineIssue, Long> getSearchQueryBuilder(String search) throws SQLException {
		RedmineFilter filter = getParameter();
		QueryBuilder<RedmineIssue, Long> builder = dao.queryBuilder();
		Where<RedmineIssue, Long> where = builder.where();
		where
				.like(RedmineIssue.SUBJECT, "%" + search + "%")
				.or()
				.like(RedmineIssue.DESCRIPTION, "%" + search + "%")
		;
		if(TextUtils.isDigitsOnly(search)){
			where.or().eq(RedmineIssue.ISSUE_ID, search);
		}
		where.and();

		setupWhere(filter, where);
		builder.setWhere(where);
		if(TextUtils.isEmpty(filter.getSort())){
			builder.orderBy(RedmineIssue.ISSUE_ID, false);
		} else {
			for(RedmineFilterSortItem key : filter.getSortList()){
				builder.orderBy(key.getDbKey(),key.isAscending());
			}
		}
		Log.d(TAG, builder.prepareStatementString());
		return builder;
	}

	protected void setupWhere(RedmineFilter filter,
								Where<RedmineIssue, Long> where) throws SQLException {
		Hashtable<String, Object> dic = new Hashtable<>();
		if(filter.getConnectionId() != null) dic.put(RedmineFilter.CONNECTION,	filter.getConnectionId());
		if(filter.getProject()	 != null) dic.put(RedmineFilter.PROJECT,		filter.getProject()		);
		if(filter.getTracker()	 != null) dic.put(RedmineFilter.TRACKER,		filter.getTracker()		);
		if(filter.getAssigned()	 != null) dic.put(RedmineFilter.ASSIGNED,		filter.getAssigned()	);
		if(filter.getAuthor()	 != null) dic.put(RedmineFilter.AUTHOR,			filter.getAuthor()		);
		if(filter.getCategory()	 != null) dic.put(RedmineFilter.CATEGORY,		filter.getCategory()	);
		if(filter.getStatus()	 != null) dic.put(RedmineFilter.STATUS,			filter.getStatus()		);
		if(filter.getVersion()	 != null) dic.put(RedmineFilter.VERSION,		filter.getVersion()		);
		if(filter.getPriority()	 != null) dic.put(RedmineFilter.PRIORITY,		filter.getPriority()	);
	
		boolean isFirst = true;
		if(filter.isClosed() != null) {
			isFirst = false;
			if(filter.isClosed())
				where.isNotNull(RedmineFilter.CLOSED);
			else
				where.isNull(RedmineFilter.CLOSED);
		}
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
}
