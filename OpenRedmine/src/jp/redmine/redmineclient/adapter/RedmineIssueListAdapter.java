package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineIssueListItemForm;

import android.view.LayoutInflater;
import android.view.View;

public class RedmineIssueListAdapter extends RedmineBaseAdapter<RedmineIssue> {
	private RedmineIssueModel model;
	protected int connection_id;
	protected long project_id;
	public RedmineIssueListAdapter(RedmineIssueModel m, int connection, long project) {
		super();
		model = m;
		connection_id = connection;
		project_id = project;
	}

	@Override
	protected View getItemView(LayoutInflater infalInflater) {
		return infalInflater.inflate(R.layout.issueitem, null);
	}

	@Override
	protected void setupView(View view, RedmineIssue data) {
		RedmineIssueListItemForm form = new RedmineIssueListItemForm(view);
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		return (int) model.countByProject(connection_id, project_id);
	}

	@Override
	protected RedmineIssue getDbItem(int position) throws SQLException {
		return model.fetchItemByProject(connection_id, project_id,(long) position, 1L);
	}

	@Override
	protected long getDbItemId(RedmineIssue item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

}
