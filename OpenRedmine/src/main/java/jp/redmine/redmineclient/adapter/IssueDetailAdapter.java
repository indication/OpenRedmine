package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.adapter.form.IssueDetailForm;
import jp.redmine.redmineclient.adapter.form.IssueJournalHeaderForm;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class IssueDetailAdapter extends RedmineDaoAdapter<RedmineIssue, Long, DatabaseCacheHelper> implements StickyListHeadersAdapter {
	private static final String TAG = IssueDetailAdapter.class.getSimpleName();
	private RedmineTimeEntryModel mTimeEntry;
	protected Integer connection_id;
	protected Long issue_id;
	protected WebviewActionInterface action;

	public IssueDetailAdapter(DatabaseCacheHelper helper, Context context, WebviewActionInterface act) {
		super(helper, context, RedmineIssue.class);
		mTimeEntry = new RedmineTimeEntryModel(helper);
		action = act;
	}

	public void setupParameter(int connection, long issue){
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
		return R.layout.issueviewdetail;
	}

	@Override
	protected void setupView(View view, RedmineIssue data) {
		IssueDetailForm form;
		if(view.getTag() != null && view.getTag() instanceof IssueDetailForm){
			form = (IssueDetailForm)view.getTag();
		} else {
			form = new IssueDetailForm(view);
			form.setupWebView(action);
		}
		form.setValue(data);
		form.setValueTimeEntry(data.getDoneHours());
	}


	@Override
	protected RedmineIssue getDbItem(int position){
		RedmineIssue issue = super.getDbItem(position);
		try {
			if(issue != null)
				issue.setDoneHours(mTimeEntry.sumByIssueId(connection_id, issue.getIssueId()));
		} catch (SQLException e) {
			Log.e(TAG, "getDbItem", e);
		}
		return issue;
	}

	@Override
	protected long getDbItemId(RedmineIssue item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		if (convertView != null && (
				 convertView.getTag() == null
				|| ! ( (Integer)(convertView.getTag()) != R.layout.journalitemheader)
			)) {
			convertView = null;
		}
		if (convertView == null) {
			convertView = infrator.inflate(R.layout.journalitemheader, null);
			convertView.setTag(R.layout.journalitemheader);
		}
		if(convertView != null){
			RedmineIssue rec = super.getDbItem(position);
			IssueJournalHeaderForm form = new IssueJournalHeaderForm(convertView);
			form.setValue(rec);
		}
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return getItemId(position);
	}

	@Override
	protected QueryBuilder<RedmineIssue, Long> getQueryBuilder() throws SQLException {
		QueryBuilder<RedmineIssue, Long> builder = dao.queryBuilder();
		Where<RedmineIssue,Long> where = builder.where()
				//.eq(RedmineIssue.CONNECTION, connection_id)
				//.and()
				.eq(RedmineIssue.ID, issue_id)
				;
		builder.setWhere(where);
		return builder;
	}
}
