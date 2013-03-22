package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineTracker;


public class RedmineTrackerModel implements IMasterModel<RedmineTracker> {
	protected Dao<RedmineTracker, Integer> dao;
	public RedmineTrackerModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineTracker.class);
		} catch (SQLException e) {
			Log.e("RedmineTrackerModel","getDao",e);
		}
	}

	public List<RedmineTracker> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineTracker> fetchAll(int connection) throws SQLException{
		List<RedmineTracker> item;
		item = dao.queryForEq(RedmineTracker.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineTracker>();
		}
		return item;
	}

	public RedmineTracker fetchById(int connection, int statusId) throws SQLException{
		PreparedQuery<RedmineTracker> query = dao.queryBuilder().where()
		.eq(RedmineTracker.CONNECTION, connection)
		.and()
		.eq(RedmineTracker.STATUS_ID, statusId)
		.prepare();
		RedmineTracker item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineTracker();
		return item;
	}

	public RedmineTracker fetchById(int id) throws SQLException{
		RedmineTracker item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineTracker();
		return item;
	}

	public int insert(RedmineTracker item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineTracker item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineTracker item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(int id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}

	public void refreshItem(RedmineIssue data) throws SQLException{
		RedmineTracker item = refreshItem(data.getConnectionId(),data.getTracker());
		data.setTracker(item);
	}
	public RedmineTracker refreshItem(RedmineConnection info,RedmineTracker data) throws SQLException{
		return refreshItem(info.getId(),data);
	}
	public RedmineTracker refreshItem(int connection_id,RedmineTracker data) throws SQLException{
		if(data == null)
			return null;
		RedmineTracker project = this.fetchById(connection_id, data.getTrackerId());
		data.setConnectionId(connection_id);
		if(project.getId() == null){
			this.insert(data);
			project = fetchById(connection_id, data.getTrackerId());
		} else {
			data.setId(project.getId());
			if(project.getModified() == null){
				project.setModified(new java.util.Date());
			}
			if(data.getModified() == null){
				data.setModified(new java.util.Date());
			}
			if (project.getModified().after(data.getModified())){
				this.update(data);
			}
		}
		return project;
	}

	@Override
	public long countByProject(int connection_id, long project_id) throws SQLException {
		QueryBuilder<RedmineTracker, ?> builder = dao.queryBuilder();
		builder
			.setCountOf(true)
			.where()
				.eq(RedmineTracker.CONNECTION, connection_id)
				//.and()
				//.eq(RedmineStatus.PROJECT_ID, project_id)
				;
		return dao.countOf(builder.prepare());
	}

	@Override
	public RedmineTracker fetchItemByProject(int connection_id,
			long project_id, long offset, long limit) throws SQLException {
		QueryBuilder<RedmineTracker, ?> builder = dao.queryBuilder();
		builder
			.limit(limit)
			.offset(offset)
			.orderBy(RedmineTracker.NAME, true)
			.where()
				.eq(RedmineTracker.CONNECTION, connection_id)
				//.and()
				//.eq(RedmineStatus.PROJECT_ID, project_id)
				;
		RedmineTracker item = builder.queryForFirst();
		if(item == null)
			item = new RedmineTracker();
		return item;
	}

}
