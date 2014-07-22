package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.view.View;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.form.TimeEntryForm;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;

public class IssueTimeEntryListAdapter extends RedmineDaoAdapter<RedmineTimeEntry, Long, DatabaseCacheHelper> {
	private RedmineTimeEntryModel model;
	protected Integer connection_id;
	protected Integer issue_id;
	public IssueTimeEntryListAdapter(DatabaseCacheHelper helper, Context context) {
		super(helper, context, RedmineTimeEntry.class);
	}

	public void setupParameter(int connection, int issue){
		connection_id = connection;
		issue_id = issue;
	}

    @Override
	public boolean isValidParameter(){
		if(issue_id == null || connection_id == null)
			return false;
		else
			return true;
	}

	@Override
	protected int getItemViewId() {
		return R.layout.listitem_timeentry;
	}

	@Override
	protected void setupView(View view, RedmineTimeEntry data) {
		TimeEntryForm form;
		if(view.getTag() != null && view.getTag() instanceof TimeEntryForm){
			form = (TimeEntryForm)view.getTag();
		} else {
			form = new TimeEntryForm(view);
		}
		form.setValue(data);
	}

	@Override
	protected long getDbItemId(RedmineTimeEntry item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

	@Override
	protected QueryBuilder<RedmineTimeEntry, Long> getQueryBuilder() throws SQLException {
		QueryBuilder<RedmineTimeEntry, Long> builder = dao.queryBuilder();
		Where<RedmineTimeEntry,Long> where = builder.where()
				.eq(RedmineTimeEntry.CONNECTION, connection_id)
				.and()
				.eq(RedmineTimeEntry.ISSUE_ID, issue_id)
				;
		builder.setWhere(where);
		builder.orderBy(RedmineTimeEntry.ID, true);
		return builder;
	}
}
