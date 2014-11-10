package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.view.View;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;

import jp.redmine.redmineclient.adapter.form.IMasterRecordForm;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineWatcher;

public class IssueWatcherListAdapter extends RedmineDaoAdapter<RedmineWatcher, Long, DatabaseCacheHelper> {
	protected Integer connection_id;
	protected Integer issue_id;
	public IssueWatcherListAdapter(DatabaseCacheHelper helper, Context context) {
		super(helper, context, RedmineWatcher.class);
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
		return android.R.layout.simple_list_item_1;
	}

	@Override
	protected void setupView(View view, RedmineWatcher data) {
		IMasterRecordForm form;
		if(view.getTag() != null && view.getTag() instanceof IMasterRecordForm){
			form = (IMasterRecordForm)view.getTag();
		} else {
			form = new IMasterRecordForm(view);
		}
		form.setValue(data.getUser());
	}

	@Override
	protected long getDbItemId(RedmineWatcher item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

	@Override
	protected QueryBuilder<RedmineWatcher, Long> getQueryBuilder() throws SQLException {
		QueryBuilder<RedmineWatcher, Long> builder = dao.queryBuilder();
		Where<RedmineWatcher,Long> where = builder.where()
				.eq(RedmineWatcher.CONNECTION, connection_id)
				.and()
				.eq(RedmineWatcher.ISSUE_ID, issue_id)
				;
		builder.setWhere(where);
		builder.orderBy(RedmineWatcher.ID, true);
		return builder;
	}
}
