package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineRole;
import jp.redmine.redmineclient.entity.RedmineStatus;


public class RedmineRoleModel implements IMasterModel<RedmineRole> {
	private final static String TAG = RedmineRoleModel.class.getSimpleName();
	protected Dao<RedmineRole, Integer> dao;
	public RedmineRoleModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineRole.class);
		} catch (SQLException e) {
			Log.e(TAG,"getDao",e);
		}
	}

	public List<RedmineRole> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineRole> fetchAll(int connection) throws SQLException{
		List<RedmineRole> item;
		item = dao.queryForEq(RedmineStatus.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<>();
		}
		return item;
	}

	public RedmineRole fetchById(int connection, int statusId) throws SQLException{
		PreparedQuery<RedmineRole> query = dao.queryBuilder().where()
		.eq(RedmineRole.CONNECTION, connection)
		.and()
		.eq(RedmineRole.ROLE_ID, statusId)
		.prepare();
		Log.d(TAG,query.getStatement());
		List<RedmineRole> items = dao.query(query);
		return items.size() < 1 ? new RedmineRole() : items.get(0);
	}

	public RedmineRole fetchById(int id) throws SQLException{
		RedmineRole item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineRole();
		return item;
	}


	@Override
	public long countByProject(int connection_id, long project_id) throws SQLException {
		QueryBuilder<RedmineRole, ?> builder = dao.queryBuilder();
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
	public RedmineRole fetchItemByProject(int connection_id,
			long project_id, long offset, long limit) throws SQLException {
		QueryBuilder<RedmineRole, ?> builder = dao.queryBuilder();
		builder
			.limit(limit)
			.offset(offset)
			.orderBy(RedmineStatus.NAME, true)
			.where()
				.eq(RedmineStatus.CONNECTION, connection_id)
				//.and()
				//.eq(RedmineStatus.PROJECT_ID, project_id)
				;
		List<RedmineRole> items = builder.query();
		return items.size() < 1 ? new RedmineRole() : items.get(0);
	}

	public int insert(RedmineRole item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedmineRole item) throws SQLException{
		return dao.update(item);
	}
	public int delete(RedmineRole item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(int id) throws SQLException{
		return dao.deleteById(id);
	}

	public RedmineRole refreshItem(RedmineConnection info,RedmineRole data) throws SQLException{
		return refreshItem(info.getId(),data);
	}
	public RedmineRole refreshItem(int connection_id,RedmineRole data) throws SQLException{
		if(data == null)
			return null;

		RedmineRole project = this.fetchById(connection_id, data.getRoleId());
		data.setConnectionId(connection_id);
		if(project.getId() == null){
			this.insert(data);
			project = fetchById(connection_id, data.getRoleId());
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

		return project;
	}
}
