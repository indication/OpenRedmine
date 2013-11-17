package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineIssueViewDetailForm;
import jp.redmine.redmineclient.form.RedmineJournalListItemHeaderForm;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class RedmineIssueDetailAdapter extends RedmineBaseAdapter<RedmineIssue> implements StickyListHeadersAdapter {
	private RedmineIssueModel mIssue;
	private RedmineTimeEntryModel mTimeEntry;
	protected Integer connection_id;
	protected Long issue_id;
	protected WebviewActionInterface action;
	private RedmineIssue cache;


	public RedmineIssueDetailAdapter(DatabaseCacheHelper m,WebviewActionInterface act){
		super();
		mIssue = new RedmineIssueModel(m);
		mTimeEntry = new RedmineTimeEntryModel(m);
		action = act;
	}

	public void setupParameter(int connection, long issue){
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
		return R.layout.issueviewdetail;
	}

	@Override
	protected void setupView(View view, RedmineIssue data) {
		RedmineIssueViewDetailForm form = new RedmineIssueViewDetailForm(view);
		form.setupWebView(action);
		form.setValue(data);
		form.setValueTimeEntry(data.getDoneHours());
	}

	@Override
	protected int getDbCount() throws SQLException {
		if(!isValidParameter())
			return 0;
		return getDbItem(0) == null ? 0 : 1;
	}

	@Override
	protected RedmineIssue getDbItem(int position) throws SQLException {
		if(!isValidParameter())
			return null;
		if(cache != null)
			return cache;
		RedmineIssue issue = mIssue.fetchById(issue_id.intValue());
		issue.setDoneHours(mTimeEntry.sumByIssueId(connection_id, issue.getIssueId()));
		cache = issue;
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
			LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.journalitemheader, null);
			convertView.setTag(R.layout.journalitemheader);
		}
		if(convertView != null){
			RedmineIssue rec = getItemWithCache(position);
			RedmineJournalListItemHeaderForm form = new RedmineJournalListItemHeaderForm(convertView);
			form.setValue(rec);
		}
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return getItemId(position);
	}

}
