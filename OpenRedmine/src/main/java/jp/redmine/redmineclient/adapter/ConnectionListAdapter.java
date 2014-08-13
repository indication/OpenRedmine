package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.db.store.DatabaseHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.adapter.form.ConnectionForm;

import android.content.Context;
import android.view.View;

import com.j256.ormlite.stmt.QueryBuilder;

public class ConnectionListAdapter extends RedmineDaoAdapter<RedmineConnection, Integer, DatabaseHelper> {
	private static final String TAG = ConnectionListAdapter.class.getSimpleName();

	public ConnectionListAdapter(DatabaseHelper helper, Context context) {
		super(helper, context, RedmineConnection.class);
	}

	@Override
	public boolean isValidParameter(){
		return true;
	}

	@Override
	protected int getItemViewId() {
		return android.R.layout.simple_list_item_1;
	}

	@Override
	protected void setupView(View view, RedmineConnection data) {
		ConnectionForm form;
		if(view.getTag() != null && view.getTag() instanceof ConnectionForm){
			form = (ConnectionForm)view.getTag();
		} else {
			form = new ConnectionForm(view);
		}
		form.setValue(data);
	}

	@Override
	protected QueryBuilder<RedmineConnection, Integer> getQueryBuilder() throws SQLException {
		QueryBuilder<RedmineConnection, Integer> builder = dao.queryBuilder();
		builder.orderBy(RedmineConnection.ID, true);
		return builder;
	}

	@Override
	protected long getDbItemId(RedmineConnection item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

}
