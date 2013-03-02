package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.RedmineProjectListItemForm;

import android.view.LayoutInflater;
import android.view.View;

public class RedmineProjectListAdapter extends RedmineBaseAdapter<RedmineProject> {
	private RedmineProjectModel model;
	protected Integer connection_id;
	public RedmineProjectListAdapter(DatabaseCacheHelper helper) {
		super();
		model = new RedmineProjectModel(helper);
	}

	public void setupParameter(int connection){
		connection_id = connection;
	}

	public boolean isValidParameter(){
		if(connection_id == null)
			return false;
		else
			return true;
	}

	@Override
	protected View getItemView(LayoutInflater infalInflater) {
		return infalInflater.inflate(android.R.layout.simple_list_item_1, null);
	}

	@Override
	protected void setupView(View view, RedmineProject data) {
		RedmineProjectListItemForm form = new RedmineProjectListItemForm(view);
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		if(!isValidParameter())
			return 0;
		return (int) model.countByProject(connection_id,0);
	}

	@Override
	protected RedmineProject getDbItem(int position) throws SQLException {
		if(!isValidParameter())
			return new RedmineProject();
		return model.fetchItemByProject(connection_id,0,(long) position, 1L);
	}

	@Override
	protected long getDbItemId(RedmineProject item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

}
