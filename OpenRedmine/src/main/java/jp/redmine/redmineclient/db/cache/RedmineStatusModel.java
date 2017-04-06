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
import jp.redmine.redmineclient.entity.RedmineStatus;


public class RedmineStatusModel implements IMasterModel<RedmineStatus> {
	private final static String TAG = RedmineStatusModel.class.getSimpleName();
	protected Dao<RedmineStatus, Integer> dao;
	public RedmineStatusModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineStatus.class);
		} catch (SQLException e) {
			Log.e(TAG,"getDao",e);
		}
	}

	public List<RedmineStatus> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineStatus> fetchAll(int connection) throws SQLException{
		List<RedmineStatus> item;
		item = dao.queryForEq(RedmineStatus.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<>();
		}
		return item;
	}

	public RedmineStatus fetchById(int connection, int statusId) throws SQLException{
		PreparedQuery<RedmineStatus> query = dao.queryBuilder().where()
		.eq(RedmineStatus.CONNECTION, connection)
		.and()
		.eq(RedmineStatus.STATUS_ID, statusId)
		.prepare();
		Log.d(TAG,query.getStatement());
		List<RedmineStatus> items = dao.query(query);
		return items.size() < 1 ? new RedmineStatus() : items.get(0);
	}

	public RedmineStatus fetchById(int id) throws SQLException{
		RedmineStatus item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineStatus();
		return item;
	}


	@Override
	public long countByProject(int connection_id, long project_id) throws SQLException {
		QueryBuilder<RedmineStatus, ?> builder = dao.queryBuilder();
		builder
			.setCountOf(true)
			.where()
				.eq(RedmineStatus.CONNECTION, connection_id)
				//.and()
				//.eq(RedmineStatus.PROJECT_ID, project_id)
				;
		return dao.countOf(builder.prepare());
	}

	@Override
	public RedmineStatus fetchItemByProject(int connection_id,
			long project_id, long offset, long limit) throws SQLException {
		QueryBuilder<RedmineStatus, ?> builder = dao.queryBuilder();
		builder
			.limit(limit)
			.offset(offset)
			.orderBy(RedmineStatus.NAME, true)
			.where()
				.eq(RedmineStatus.CONNECTION, connection_id)
				//.and()
				//.eq(RedmineStatus.PROJECT_ID, project_id)
				;
		List<RedmineStatus> items = builder.query();
		return items.size() < 1 ? new RedmineStatus() : items.get(0);
	}

	public int insert(RedmineStatus item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedmineStatus item) throws SQLException{
		return dao.update(item);
	}
	public int delete(RedmineStatus item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(int id) throws SQLException{
		return dao.deleteById(id);
	}

	public void refreshItem(RedmineIssue data) throws SQLException{
		RedmineStatus item = refreshItem(data.getConnectionId(),data.getStatus());

		if(!item.isClose() && data.getClosed() != null)
			data.setClosed(null);
		data.setStatus(item);
	}
	public RedmineStatus refreshItem(RedmineConnection info,RedmineStatus data) throws SQLException{
		return refreshItem(info.getId(),data);
	}
	public RedmineStatus refreshItem(int connection_id,RedmineStatus data) throws SQLException{
		if(data == null)
			return null;

		RedmineStatus project = this.fetchById(connection_id, data.getStatusId());
		data.setConnectionId(connection_id);
		if(project.getId() == null){
			this.insert(data);
			project = fetchById(connection_id, data.getStatusId());
		} else {
			data.setId(project.getId());
			if(project.getModified() == null){
				project.setModified(new java.util.Date());
			}
			if(data.getModified() == null){
				data.setModified(new java.util.Date());
			}
			if(project.getModified().after(data.getModified())){
				this.update(data);
			}
		}

		return project;
	}
}
