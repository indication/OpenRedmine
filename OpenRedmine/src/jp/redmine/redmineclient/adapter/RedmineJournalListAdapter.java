package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.RedmineJournalModel;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.form.RedmineJournalListItemForm;
import android.view.LayoutInflater;
import android.view.View;

public class RedmineJournalListAdapter extends RedmineBaseAdapter<RedmineJournal> {

	private RedmineJournalModel model;
	protected int connection_id;
	protected long issue_id;



	public RedmineJournalListAdapter(RedmineJournalModel m, int connection, long issue){
		super();
		model = m;
		connection_id = connection;
		issue_id = issue;
	}

	@Override
	protected View getItemView(LayoutInflater infalInflater) {
		return  infalInflater.inflate(R.layout.journalitem, null);
	}

	@Override
	protected void setupView(View view, RedmineJournal data) {
		RedmineJournalListItemForm form = new RedmineJournalListItemForm(view);
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		return (int) model.countByIssue(connection_id, issue_id);
	}

	@Override
	protected RedmineJournal getDbItem(int position) throws SQLException {
		return model.fetchItemByIssue(connection_id, issue_id,(long) position, 1);
	}

	@Override
	protected long getDbItemId(RedmineJournal item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}


}
