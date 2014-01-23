package jp.redmine.redmineclient.adapter;

import android.view.View;

import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.db.cache.RedmineWikiModel;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineWiki;
import jp.redmine.redmineclient.form.IMasterRecordListItemForm;
import jp.redmine.redmineclient.form.RedmineWikiListItemForm;

public class RedmineWikiListAdapter extends RedmineBaseAdapter<RedmineWiki> {
	private RedmineWikiModel model;
	protected Integer connection_id;
	protected Long project_id;
	public RedmineWikiListAdapter(DatabaseCacheHelper helper) {
		super();
		model = new RedmineWikiModel(helper);
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
	protected void setupView(View view, RedmineWiki data) {
		RedmineWikiListItemForm form = new RedmineWikiListItemForm(view);
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		return (int) model.countByProject(connection_id,project_id);
	}

	@Override
	protected RedmineWiki getDbItem(int position) throws SQLException {
		return model.fetchItemByProject(connection_id,project_id,(long) position, 1L);
	}

	@Override
	protected long getDbItemId(RedmineWiki item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

}
