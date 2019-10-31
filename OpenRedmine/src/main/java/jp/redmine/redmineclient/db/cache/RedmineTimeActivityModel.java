package jp.redmine.redmineclient.db.cache;

import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTimeActivity;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;


public class RedmineTimeActivityModel implements IMasterModel<RedmineTimeActivity> {
	private final static String TAG = RedmineTimeActivityModel.class.getSimpleName();
	protected Dao<RedmineTimeActivity, Integer> dao;
	public RedmineTimeActivityModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineTimeActivity.class);
		} catch (SQLException e) {
			Log.e(TAG,"getDao",e);
		}
	}

	public List<RedmineTimeActivity> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineTimeActivity> fetchAll(int connection) throws SQLException{
		List<RedmineTimeActivity> item;
		item = dao.queryForEq(RedmineTimeActivity.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<>();
		}
		return item;
	}

	public RedmineTimeActivity fetchById(int connection, int statusId) throws SQLException{
		PreparedQuery<RedmineTimeActivity> query = dao.queryBuilder().where()
		.eq(RedmineTimeActivity.CONNECTION, connection)
		.and()
		.eq(RedmineTimeActivity.ACTIVITY_ID, statusId)
		.prepare();
		Log.d(TAG,query.getStatement());
		List<RedmineTimeActivity> items = dao.query(query);
		return items.size() < 1 ? new RedmineTimeActivity() : items.get(0);
	}

	public RedmineTimeActivity fetchById(int id) throws SQLException{
		RedmineTimeActivity item = dao.queryForId(id);
		if(item == null)
			item = new RedmineTimeActivity();
		return item;
	}


	@Override
	public long countByProject(int connection_id, long project_id) throws SQLException {
		QueryBuilder<RedmineTimeActivity, ?> builder = dao.queryBuilder();
		builder
			.setCountOf(true)
			.where()
				.eq(RedmineTimeActivity.CONNECTION, connection_id)
				;
		return dao.countOf(builder.prepare());
	}

	@Override
	public RedmineTimeActivity fetchItemByProject(int connection_id,
			long project_id, long offset, long limit) throws SQLException {
		QueryBuilder<RedmineTimeActivity, ?> builder = dao.queryBuilder();
		builder
			.limit(limit)
			.offset(offset)
			//.orderBy(RedmineTimeActivity.NAME, true)
			.where()
				.eq(RedmineTimeActivity.CONNECTION, connection_id)
				;
		List<RedmineTimeActivity> items = builder.query();
		return items.size() < 1 ? new RedmineTimeActivity() : items.get(0);
	}

	public int insert(RedmineTimeActivity item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedmineTimeActivity item) throws SQLException{
		return dao.update(item);
	}
	public int delete(RedmineTimeActivity item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(int id) throws SQLException{
		return dao.deleteById(id);
	}

	public void refreshItem(RedmineTimeEntry data) throws SQLException{
		if(data == null || data.getActivity() == null)
			return;
		RedmineTimeActivity item = refreshItem(data.getConnectionId(),data.getActivity());
		if(item == null)
			return;
		data.setActivity(item);
	}
	public RedmineTimeActivity refreshItem(RedmineConnection info,RedmineTimeActivity data) throws SQLException{
		return refreshItem(info.getId(),data);
	}
	public RedmineTimeActivity refreshItem(int connection_id,RedmineTimeActivity data) throws SQLException{
		if(data == null)
			return null;

		RedmineTimeActivity timeentry = this.fetchById(connection_id, data.getActivityId());
		data.setConnectionId(connection_id);
		if(timeentry.getId() == null){
			this.insert(data);
			timeentry = fetchById(connection_id, data.getActivityId());
		} else {
			data.setId(timeentry.getId());
			if(timeentry.getModified() == null){
				timeentry.setModified(new java.util.Date());
			}
			if(data.getModified() == null){
				data.setModified(new java.util.Date());
			}
			/*
			 * This data is provided without date.
			 * Modified date or name was chnaged then update.
			 */
			if(timeentry.getModified().after(data.getModified())
			||	(!TextUtils.isEmpty(data.getName())
				&& !data.getName().equals(timeentry.getName()))
			){
				this.update(data);
			}
		}

		return timeentry;
	}
}
