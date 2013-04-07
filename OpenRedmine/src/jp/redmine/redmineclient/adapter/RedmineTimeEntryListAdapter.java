package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.form.RedmineTimeEntryListItemForm;

import android.view.LayoutInflater;
import android.view.View;

public class RedmineTimeEntryListAdapter extends RedmineBaseAdapter<RedmineTimeEntry> {
	private RedmineTimeEntryModel model;
	protected Integer connection_id;
	protected Long project_id;
	public RedmineTimeEntryListAdapter(DatabaseCacheHelper helper) {
		super();
		model = new RedmineTimeEntryModel(helper);

	}

	public void setupParameter(int connection, long project){
		connection_id = connection;
		project_id = project;
	}

	public boolean isValidParameter(){
		if(project_id == null || connection_id == null)
			return false;
		else
			return true;
	}

	@Override
	protected View getItemView(LayoutInflater infalInflater) {
		return infalInflater.inflate(R.layout.timeentryitem, null);
	}

	@Override
	protected void setupView(View view, RedmineTimeEntry data) {
		RedmineTimeEntryListItemForm form = new RedmineTimeEntryListItemForm(view);
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		return (int) model.countByProject(connection_id, project_id);
	}

	@Override
	protected RedmineTimeEntry getDbItem(int position) throws SQLException {
		return model.fetchItemByProject(connection_id, project_id, position, 1L);
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
