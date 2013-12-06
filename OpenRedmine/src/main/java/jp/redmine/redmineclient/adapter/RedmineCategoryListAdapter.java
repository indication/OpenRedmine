package jp.redmine.redmineclient.adapter;

import android.view.View;

import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineCategoryModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.form.IMasterRecordListItemForm;

public class RedmineCategoryListAdapter extends RedmineBaseAdapter<RedmineProjectCategory> {
	private RedmineCategoryModel model;
	protected Integer connection_id;
	protected Long project_id;
	public RedmineCategoryListAdapter(DatabaseCacheHelper helper) {
		super();
		model = new RedmineCategoryModel(helper);
	}

	public void setupParameter(int connection, long project){
		connection_id = connection;
		project_id = project;
	}

    @Override
	public boolean isValidParameter(){
		if(connection_id == null || project_id == null)
			return false;
		else
			return true;
	}

	@Override
	protected int getItemViewId() {
		return android.R.layout.simple_list_item_1;
	}

	@Override
	protected void setupView(View view, RedmineProjectCategory data) {
		IMasterRecordListItemForm form = new IMasterRecordListItemForm(view);
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		return (int) model.countByProject(connection_id,project_id);
	}

	@Override
	protected RedmineProjectCategory getDbItem(int position) throws SQLException {
		return model.fetchItemByProject(connection_id,project_id,(long) position, 1L);
	}

	@Override
	protected long getDbItemId(RedmineProjectCategory item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

}
