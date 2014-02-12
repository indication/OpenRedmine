package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.model.ConnectionModel;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class RedmineProjectFavoriteAdapter extends BaseAdapter implements StickyListHeadersAdapter {
	private static final String TAG = RedmineProjectListAdapter.class.getSimpleName();
	private LayoutInflater infrator;
	protected Dao<RedmineProject, Long> dao;
	private AndroidDatabaseResults dbResults;
	private ConnectionModel mConnection;
	private int count = 0;

	public RedmineProjectFavoriteAdapter(DatabaseCacheHelper helper, Context context){
		super();
		infrator = LayoutInflater.from(context);
		try {
			dao = helper.getDao(RedmineProject.class);
		} catch (SQLException e) {
			Log.e(TAG,TAG,e);
		}
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
		return convertView;
	}

	@Override
	public long getHeaderId(int i) {
		RedmineProject proj = (RedmineProject)getItem(i);
		return proj == null ? 0 : proj.getConnectionId();
	}

	@Override
	public int getCount() {
		return (dbResults == null) ? 0 : dbResults.getCount();
	}

	@Override
	public Object getItem(int i) {
		if(dbResults == null)
			return null;
		dbResults.moveAbsolute(i);
		try {
			return dao.mapSelectStarRow(dbResults);
		} catch (SQLException e) {
			Log.e(TAG, "getItem", e);
		}
		return null;
	}

	@Override
	public long getItemId(int i) {
		RedmineProject proj = (RedmineProject)getItem(i);
		return proj == null ? 0 : proj.getId();
	}

	@Override
	public View getView(int i, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = infrator.inflate(android.R.layout.simple_list_item_1, null);
		}
		if(convertView == null)
			return null;
		TextView text = (TextView)convertView.findViewById(android.R.id.text1);
		RedmineProject proj = (RedmineProject)getItem(i);
		text.setText(TextUtils.isEmpty(proj.getName()) ? "" : proj.getName());
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		try {
			QueryBuilder<RedmineProject, Long> builder = dao.queryBuilder();
			builder.setWhere(builder.where().gt(RedmineProject.FAVORITE, 0));
			builder.orderBy(RedmineProject.CONNECTION, true);
			dbResults = (AndroidDatabaseResults) dao.iterator(builder.prepare()).getRawResults();
		} catch (SQLException e) {
			Log.e(TAG, "notifyDataSetChanged", e);
		}
		super.notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetInvalidated() {
		if(dbResults != null){
			dbResults.closeQuietly();
			dbResults = null;
		}
		super.notifyDataSetInvalidated();
	}
}
