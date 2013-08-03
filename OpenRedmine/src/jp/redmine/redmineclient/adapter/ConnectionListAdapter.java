package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.db.store.DatabaseHelper;
import jp.redmine.redmineclient.db.store.RedmineConnectionModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.form.RedmineConnectionListItemForm;

import android.view.LayoutInflater;
import android.view.View;

public class ConnectionListAdapter extends RedmineBaseAdapter<RedmineConnection> {
	private RedmineConnectionModel model;
	public ConnectionListAdapter(DatabaseHelper helper) {
		super();
		model = new RedmineConnectionModel(helper);
	}

	public void setupParameter(int connection){
	}

	public boolean isValidParameter(){
		return true;
	}

	@Override
	protected View getItemView(LayoutInflater infalInflater) {
		return infalInflater.inflate(android.R.layout.simple_list_item_1, null);
	}

	@Override
	protected void setupView(View view, RedmineConnection data) {
		RedmineConnectionListItemForm form = new RedmineConnectionListItemForm(view);
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		if(!isValidParameter())
			return 0;
		return (int) model.countByProject(0,0);
	}

	@Override
	protected RedmineConnection getDbItem(int position) throws SQLException {
		if(!isValidParameter())
			return new RedmineConnection();
		return model.fetchItemByProject(0,0,(long) position, 1L);
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
