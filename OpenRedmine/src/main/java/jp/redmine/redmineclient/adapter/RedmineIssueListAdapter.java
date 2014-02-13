package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineIssueListItemForm;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class RedmineIssueListAdapter extends RedmineDaoAdapter<RedmineIssue, Long, DatabaseCacheHelper> {
	private static final String TAG = RedmineIssueListAdapter.class.getSimpleName();
	private RedmineFilterModel mFilter;
	protected Integer connection_id;
	protected Long project_id;
	protected Integer filter_id;
	public RedmineIssueListAdapter(DatabaseCacheHelper helper, Context context) {
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
		return R.layout.issueitem;
	}

	@Override
	protected void setupView(View view, RedmineIssue data) {
		RedmineIssueListItemForm form = new RedmineIssueListItemForm(view);
		form.setValue(data);
	}

	@Override
	protected QueryBuilder<RedmineIssue, Long> getQueryBuilder() throws SQLException {
		RedmineFilter filter = getParameter();
		return getQueryBuilder(filter);
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
		RedmineFilter filter;
		try {
			if(filter_id != null)
				filter = mFilter.fetchById(filter_id);
			else
				filter = mFilter.fetchByCurrent(connection_id, project_id);
		} catch (SQLException e) {
			Log.e(TAG, "getParameter", e);
			filter = new RedmineFilter();
		}
		return filter;
	}

	protected QueryBuilder<RedmineIssue, Long> getQueryBuilder(RedmineFilter filter) throws SQLException{
		Hashtable<String, Object> dic = new Hashtable<String, Object>();
		if(filter.getConnectionId() != null) dic.put(RedmineFilter.CONNECTION,	filter.getConnectionId());
		if(filter.getProject()	 != null) dic.put(RedmineFilter.PROJECT,		filter.getProject()		);
		if(filter.getTracker()	 != null) dic.put(RedmineFilter.TRACKER,		filter.getTracker()		);
		if(filter.getAssigned()	 != null) dic.put(RedmineFilter.ASSIGNED,		filter.getAssigned()	);
		if(filter.getAuthor()	 != null) dic.put(RedmineFilter.AUTHOR,			filter.getAuthor()		);
		if(filter.getCategory()	 != null) dic.put(RedmineFilter.CATEGORY,		filter.getCategory()	);
		if(filter.getStatus()	 != null) dic.put(RedmineFilter.STATUS,			filter.getStatus()		);
		if(filter.getVersion()	 != null) dic.put(RedmineFilter.VERSION,		filter.getVersion()		);
		if(filter.getPriority()	 != null) dic.put(RedmineFilter.PRIORITY,		filter.getPriority()	);

		QueryBuilder<RedmineIssue, Long> builder = dao.queryBuilder();
		Where<RedmineIssue, Long> where = builder.where();
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
}
