package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineStatus;
import android.util.Log;


public class RedminePriorityModel {
	protected Dao<RedminePriority, Integer> dao;
	public RedminePriorityModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedminePriority.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<RedminePriority> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedminePriority> fetchAll(int connection) throws SQLException{
		List<RedminePriority> item;
		item = dao.queryForEq(RedmineStatus.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedminePriority>();
		}
		return item;
	}

	public RedminePriority fetchById(int connection, int statusId) throws SQLException{
		PreparedQuery<RedminePriority> query = dao.queryBuilder().where()
		.eq(RedminePriority.CONNECTION, connection)
		.and()
		.eq(RedminePriority.PRIORITY_ID, statusId)
		.prepare();
		Log.d("RedmineProject",query.getStatement());
		RedminePriority item = dao.queryForFirst(query);
		if(item == null)
			item = new RedminePriority();
		return item;
	}

	public RedminePriority fetchById(int id) throws SQLException{
		RedminePriority item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedminePriority();
		return item;
	}

	public int insert(RedminePriority item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedminePriority item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedminePriority item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(int id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}

	public void refreshItem(RedmineIssue data) throws SQLException{
		RedminePriority item = refreshItem(data.getConnectionId(),data.getPriority());
		data.setPriority(item);
	}
	public RedminePriority refreshItem(RedmineConnection info,RedminePriority data) throws SQLException{
		return refreshItem(info.getId(),data);
	}
	public RedminePriority refreshItem(int connection_id,RedminePriority data) throws SQLException{
		if(data == null)
			return null;

		RedminePriority project = this.fetchById(connection_id, data.getPriorityId());
		if(project.getId() == null){
			data.setConnectionId(connection_id);
			this.insert(data);
		} else {

			if(project.getModified() == null){
				project.setModified(new java.util.Date());
			}
			if(data.getModified() == null){
				data.setModified(new java.util.Date());
			}
			if(project.getModified().after(data.getModified())){
				data.setId(project.getId());
				data.setConnectionId(connection_id);
				this.update(data);
			}
		}
		return data;
	}
}
