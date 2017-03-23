package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.form.IssueForm;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineRecentIssue;
import jp.redmine.redmineclient.form.helper.HtmlHelper;
import jp.redmine.redmineclient.model.ConnectionModel;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class RecentIssueListAdapter extends RedmineDaoAdapter<RedmineRecentIssue, Long, DatabaseCacheHelper> implements StickyListHeadersAdapter {
	private ConnectionModel mConnection;

	public RecentIssueListAdapter(DatabaseCacheHelper helper, Context context){
		super(helper, context, RedmineRecentIssue.class);
		mConnection = new ConnectionModel(context);
	}
	@Override
	public View getHeaderView(int i, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = infrator.inflate(R.layout.listheader_connection, parent, false);
			if (convertView == null)
				return null;
		}
		RedmineConnection connection = mConnection.getItem((int)getHeaderId(i));
		TextView text = (TextView)convertView.findViewById(R.id.name);
		if(text != null)
			text.setText((TextUtils.isEmpty(connection.getName())) ? "" : connection.getName());
		//fix background to hide transparent headers
		convertView.setBackgroundColor(HtmlHelper.getBackgroundColor(convertView.getContext()));
		return convertView;
	}

	@Override
	public long getHeaderId(int i) {
		RedmineRecentIssue proj = (RedmineRecentIssue)getItem(i);
		return proj == null ? 0 : proj.getConnectionId();
	}

	@Override
	protected long getDbItemId(RedmineRecentIssue item) {
		return item.getId();
	}

	@Override
	protected int getItemViewId() {
		return R.layout.listitem_issue;
	}

	@Override
	protected void setupView(View view, RedmineRecentIssue history) {
		IssueForm form = new IssueForm(view);
		form.setValue(history);
	}

	@Override
	protected QueryBuilder getQueryBuilder() throws SQLException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, -14);
		QueryBuilder<RedmineRecentIssue, Long> builder = dao.queryBuilder();
		builder.setWhere(builder.where().ge(RedmineRecentIssue.MODIFIED, cal.getTime()));
		builder.orderBy(RedmineRecentIssue.CONNECTION, true);
		builder.orderBy(RedmineRecentIssue.MODIFIED, false);
		return builder;
	}

}
