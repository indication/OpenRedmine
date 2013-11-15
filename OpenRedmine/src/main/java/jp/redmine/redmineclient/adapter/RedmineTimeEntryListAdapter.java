package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.form.RedmineTimeEntryListItemForm;
import android.view.View;

public class RedmineTimeEntryListAdapter extends RedmineBaseAdapter<RedmineTimeEntry> {
	private RedmineTimeEntryModel model;
	protected Integer connection_id;
	protected Integer issue_id;
	public RedmineTimeEntryListAdapter(DatabaseCacheHelper helper) {
		super();
		model = new RedmineTimeEntryModel(helper);

	}

	public void setupParameter(int connection, int issue){
		connection_id = connection;
		issue_id = issue;
	}

	public boolean isValidParameter(){
		if(issue_id == null || connection_id == null)
			return false;
		else
			return true;
	}

	@Override
	protected int getItemViewId() {
		return R.layout.timeentryitem;
	}

	@Override
	protected void setupView(View view, RedmineTimeEntry data) {
		RedmineTimeEntryListItemForm form = new RedmineTimeEntryListItemForm(view);
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		return (int) model.countByProject(connection_id, issue_id);
	}

	@Override
	protected RedmineTimeEntry getDbItem(int position) throws SQLException {
		return model.fetchItemByProject(connection_id, issue_id, position, 1L);
	}

	@Override
	protected long getDbItemId(RedmineTimeEntry item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

}
