package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTimeActivity;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import android.text.TextUtils;
import android.util.Log;


public class RedmineTimeActivityModel implements IMasterModel<RedmineTimeActivity> {
	protected Dao<RedmineTimeActivity, Integer> dao;
	public RedmineTimeActivityModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineTimeActivity.class);
		} catch (SQLException e) {
			Log.e("RedmineTimeActivityModel","getDao",e);
		}
	}

	public List<RedmineTimeActivity> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineTimeActivity> fetchAll(int connection) throws SQLException{
		List<RedmineTimeActivity> item;
		item = dao.queryForEq(RedmineTimeActivity.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineTimeActivity>();
		}
		return item;
	}

	public RedmineTimeActivity fetchById(int connection, int statusId) throws SQLException{
		PreparedQuery<RedmineTimeActivity> query = dao.queryBuilder().where()
		.eq(RedmineTimeActivity.CONNECTION, connection)
		.and()
		.eq(RedmineTimeActivity.ACTIVITY_ID, statusId)
		.prepare();
		Log.d("RedmineTimeActivityModel",query.getStatement());
		RedmineTimeActivity item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineTimeActivity();
		return item;
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
		RedmineTimeActivity item = builder.queryForFirst();
		if(item == null)
			item = new RedmineTimeActivity();
		return item;
	}

	public int insert(RedmineTimeActivity item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineTimeActivity item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineTimeActivity item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(int id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
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
		if(timeentry.getId() == null){
			data.setConnectionId(connection_id);
			this.insert(data);
			timeentry = fetchById(connection_id, data.getActivityId());
		} else {
			data.setId(timeentry.getId());
			data.setConnectionId(connection_id);
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
