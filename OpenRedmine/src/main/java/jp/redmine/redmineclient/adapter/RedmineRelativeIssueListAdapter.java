package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueRelationModel;
import jp.redmine.redmineclient.entity.RedmineIssueRelation;
import jp.redmine.redmineclient.form.RedmineRelationListItemForm;
import android.view.View;

public class RedmineRelativeIssueListAdapter extends RedmineBaseAdapter<RedmineIssueRelation>  {
	@SuppressWarnings("unused")
	private static final String TAG = RedmineRelativeIssueListAdapter.class.getSimpleName();
	private RedmineIssueRelationModel mRelation;
	protected Integer connection_id;
	protected Integer issue_id;
	private RedmineIssueModel mIssue;



	public RedmineRelativeIssueListAdapter(DatabaseCacheHelper m,WebviewActionInterface act){
		super();
		mRelation = new RedmineIssueRelationModel(m);
		mIssue = new RedmineIssueModel(m);
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
		return R.layout.issuerelationitem;
	}

	@Override
	protected void setupView(View view, RedmineIssueRelation data) {
		RedmineRelationListItemForm form = new RedmineRelationListItemForm(view);
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		return (int) mRelation.countByIssue(connection_id, issue_id);
	}

	@Override
	protected RedmineIssueRelation getDbItem(int position) throws SQLException {
		RedmineIssueRelation rel = mRelation.fetchItemByIssue(connection_id, issue_id,(long) position, 1);
		rel.setIssue(mIssue.fetchById(connection_id, rel.getTargetIssueId(issue_id)));
		return rel;
	}

	@Override
	protected long getDbItemId(RedmineIssueRelation item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}



}
