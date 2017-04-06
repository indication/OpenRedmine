package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineStatus;


public class RedminePriorityModel implements IMasterModel<RedminePriority> {
	private final static String TAG = RedminePriorityModel.class.getSimpleName();
	protected Dao<RedminePriority, Integer> dao;
	public RedminePriorityModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedminePriority.class);
		} catch (SQLException e) {
			Log.e(TAG, "getDao", e);
		}
	}

	public List<RedminePriority> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedminePriority> fetchAll(int connection) throws SQLException{
		List<RedminePriority> item;
		item = dao.queryForEq(RedmineStatus.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<>();
		}
		return item;
	}

	public RedminePriority fetchById(int connection, int statusId) throws SQLException{
		PreparedQuery<RedminePriority> query = dao.queryBuilder().where()
		.eq(RedminePriority.CONNECTION, connection)
		.and()
		.eq(RedminePriority.PRIORITY_ID, statusId)
		.prepare();
		Log.d(TAG,query.getStatement());
		List<RedminePriority> items = dao.query(query);
		return items.size() < 1 ? new RedminePriority() : items.get(0);
	}

	public RedminePriority fetchById(int id) throws SQLException{
		RedminePriority item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedminePriority();
		return item;
	}

	public int insert(RedminePriority item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedminePriority item) throws SQLException{
		return dao.update(item);
	}
	public int delete(RedminePriority item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(int id) throws SQLException{
		return dao.deleteById(id);
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
		data.setConnectionId(connection_id);
		if(project.getId() == null){
			this.insert(data);
			data = fetchById(connection_id, data.getPriorityId());
		} else {
			data.setId(project.getId());

			if(project.getModified() == null){
				project.setModified(new java.util.Date());
			}
			if(data.getModified() == null){
				data.setModified(new java.util.Date());
			}
			if(!project.getModified().before(data.getModified())){
				this.update(data);
			}
		}
		return data;
	}
	@Override
	public long countByProject(int connection_id, long project_id) throws SQLException {
		QueryBuilder<RedminePriority, ?> builder = dao.queryBuilder();
		builder
			.setCountOf(true)
			.where()
				.eq(RedminePriority.CONNECTION, connection_id)
				//.and()
				//.eq(RedmineStatus.PROJECT_ID, project_id)
				;
		return dao.countOf(builder.prepare());
	}

	@Override
	public RedminePriority fetchItemByProject(int connection_id,
			long project_id, long offset, long limit) throws SQLException {
		QueryBuilder<RedminePriority, ?> builder = dao.queryBuilder();
		builder
			.limit(limit)
			.offset(offset)
			.orderBy(RedminePriority.NAME, true)
			.where()
				.eq(RedminePriority.CONNECTION, connection_id)
				//.and()
				//.eq(RedmineStatus.PROJECT_ID, project_id)
				;
		List<RedminePriority> items = builder.query();
		return items.size() < 1 ? new RedminePriority() : items.get(0);
	}
}
