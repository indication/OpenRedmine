package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.helper.HtmlHelper;
import jp.redmine.redmineclient.model.ConnectionModel;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class RedmineProjectFavoriteAdapter extends RedmineDaoAdapter<RedmineProject, Long, DatabaseCacheHelper> implements StickyListHeadersAdapter {
	private ConnectionModel mConnection;

	public RedmineProjectFavoriteAdapter(DatabaseCacheHelper helper, Context context){
		super(helper, context, RedmineProject.class);
		mConnection = new ConnectionModel(context);
	}
	@Override
	public View getHeaderView(int i, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = infrator.inflate(R.layout.connection_header, null);
		}
		if(convertView == null)
			return null;
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
		RedmineProject proj = (RedmineProject)getItem(i);
		return proj == null ? 0 : proj.getConnectionId();
	}

	@Override
	protected long getDbItemId(RedmineProject item) {
		return item.getId();
	}

	@Override
	protected int getItemViewId() {
		return android.R.layout.simple_list_item_1;
	}

	@Override
	protected void setupView(View view, RedmineProject proj) {
		TextView text = (TextView)view.findViewById(android.R.id.text1);
		text.setText(TextUtils.isEmpty(proj.getName()) ? "" : proj.getName());
	}

	@Override
	protected QueryBuilder getQueryBuilder() throws SQLException {
		QueryBuilder<RedmineProject, Long> builder = dao.queryBuilder();
		builder.setWhere(builder.where().gt(RedmineProject.FAVORITE, 0));
		builder.orderBy(RedmineProject.CONNECTION, true);
		return builder;
	}

}
