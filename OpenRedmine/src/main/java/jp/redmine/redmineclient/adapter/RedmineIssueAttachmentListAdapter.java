package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineAttachmentModel;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.form.RedmineAttachmentListItemForm;
import android.view.View;

public class RedmineIssueAttachmentListAdapter extends RedmineBaseAdapter<RedmineAttachment>  {
	@SuppressWarnings("unused")
	private static final String TAG = RedmineIssueAttachmentListAdapter.class.getSimpleName();
	private RedmineAttachmentModel mRelation;
	protected Integer connection_id;
	protected Integer issue_id;



	public RedmineIssueAttachmentListAdapter(DatabaseCacheHelper m){
		super();
		mRelation = new RedmineAttachmentModel(m);
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
		return R.layout.issueattachmentitem;
	}

	@Override
	protected void setupView(View view, RedmineAttachment data) {
		RedmineAttachmentListItemForm form = new RedmineAttachmentListItemForm(view);
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		if(!isValidParameter())
			return 0;
		return (int) mRelation.countByIssue(connection_id, issue_id);
	}

	@Override
	protected RedmineAttachment getDbItem(int position) throws SQLException {
		if(!isValidParameter())
			return null;
		RedmineAttachment rel = mRelation.fetchItemByIssue(connection_id, issue_id,(long) position, 1);
		return rel;
	}

	@Override
	protected long getDbItemId(RedmineAttachment item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}



}
