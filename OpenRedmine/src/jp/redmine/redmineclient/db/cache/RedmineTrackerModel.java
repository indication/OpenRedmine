package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineTracker;


public class RedmineTrackerModel {
	protected Dao<RedmineTracker, Integer> dao;
	public RedmineTrackerModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineTracker.class);
		} catch (SQLException e) {
			e.printStackTrace();
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
		if(project.getId() == null){
			data.setConnectionId(connection_id);
			this.insert(data);

			project = fetchById(connection_id, data.getTrackerId());
		} else {
			if(project.getModified() == null){
				project.setModified(new java.util.Date());
			}
			if(data.getModified() == null){
				data.setModified(new java.util.Date());
			}
			if (project.getModified().after(data.getModified())){
				data.setId(project.getId());
				data.setConnectionId(connection_id);
				this.update(data);
			}
		}
		return project;
	}

}
