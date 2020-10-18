package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.IUserRecord;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineUser;


public class RedmineUserModel implements IMasterModel<RedmineUser> {
	private final static String TAG = RedmineUserModel.class.getSimpleName();
	protected Dao<RedmineUser, Integer> dao;
	public RedmineUserModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineUser.class);
		} catch (SQLException e) {
			Log.e(TAG,"getDao",e);
		}
	}

	public List<RedmineUser> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineUser> fetchAll(int connection) throws SQLException{
		List<RedmineUser> item;
		item = dao.queryForEq(RedmineUser.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<>();
		}
		return item;
	}

	public RedmineUser fetchById(int connection, int userId) throws SQLException{
		PreparedQuery<RedmineUser> query = dao.queryBuilder().where()
		.eq(RedmineUser.CONNECTION, connection)
		.and()
		.eq(RedmineUser.USER_ID, userId)
		.prepare();
		List<RedmineUser> items = dao.query(query);
		return items.size() < 1 ? new RedmineUser() : items.get(0);
	}

	public RedmineUser fetchById(int id) throws SQLException{
		RedmineUser item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineUser();
		return item;
	}

	public int insert(RedmineUser item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedmineUser item) throws SQLException{
		return dao.update(item);
	}
	public int delete(RedmineUser item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(int id) throws SQLException{
		return dao.deleteById(id);
	}

	public RedmineUser fetchCurrentUser(int connection) throws SQLException{
		PreparedQuery<RedmineUser> query = dao.queryBuilder().where()
		.eq(RedmineUser.CONNECTION, connection)
		.and()
		.eq(RedmineUser.IS_CURRENT, true)
		.prepare();
		List<RedmineUser> items = dao.query(query);
		return items.size() < 1 ? null : items.get(0);
	}

	protected void clearCurrentUser(int connection_id) throws SQLException{
		UpdateBuilder<RedmineUser,Integer> builder = dao.updateBuilder();
		builder.updateColumnValue(RedmineUser.IS_CURRENT, false);
		builder.setWhere(builder.where()
				.eq(RedmineUser.CONNECTION, connection_id)
				.and()
				.eq(RedmineUser.IS_CURRENT, true));
		builder.update();
	}
	public RedmineUser refreshCurrentUser(RedmineConnection info,RedmineUser data) throws SQLException{
		return refreshCurrentUser(info.getId(),data);
	}
	public RedmineUser refreshCurrentUser(int connection_id, RedmineUser data) throws SQLException{
		clearCurrentUser(connection_id);
		data.setIsCurrent(true);
		return refreshItem(connection_id, data, true);
	}

	public void refreshItem(RedmineIssue data) throws SQLException{
		RedmineUser item;
		item = refreshItem(data.getConnectionId(),data.getAssigned(),false);
		data.setAssigned(item);
		item = refreshItem(data.getConnectionId(),data.getAuthor(),false);
		data.setAuthor(item);
	}
	public void refreshItem(IUserRecord data) throws SQLException{
		RedmineUser item;
		item = refreshItem(data.getConnectionId(),data.getUser(),false);
		data.setUser(item);
	}

	public RedmineUser refreshItem(RedmineConnection info,RedmineUser data) throws SQLException{
		return refreshItem(info.getId(),data,false);
	}
	public RedmineUser refreshItem(int connection_id,RedmineUser data,boolean isForce) throws SQLException{
		if(data == null)
			return null;
		RedmineUser project = this.fetchById(connection_id, data.getUserId());
		data.setConnectionId(connection_id);
		if(project.getId() == null){
			this.insert(data);
			project = fetchById(connection_id, data.getUserId());
		} else {
			data.setId(project.getId());

			if(project.getModified() == null){
				project.setModified(new java.util.Date());
			}
			if(data.getModified() == null){
				data.setModified(new java.util.Date());
			}
			if(!project.getModified().before(data.getModified()) || isForce){
				this.update(data);
			}
		}
		return project;
	}

	@Override
	public long countByProject(int connection_id, long project_id)
			throws SQLException {
		QueryBuilder<RedmineUser, ?> builder = dao.queryBuilder();
		builder
			.setCountOf(true)
			.where()
				.eq(RedmineUser.CONNECTION, connection_id)
				;
		return dao.countOf(builder.prepare());
	}

	@Override
	public RedmineUser fetchItemByProject(int connection_id, long project_id,
			long offset, long limit) throws SQLException {
		QueryBuilder<RedmineUser, ?> builder = dao.queryBuilder();
		builder
			.limit(limit)
			.offset(offset)
			.orderBy(RedmineUser.NAME, true)
			.where()
				.eq(RedmineUser.CONNECTION, connection_id)
				;
		List<RedmineUser> items = builder.query();
		return items.size() < 1 ? new RedmineUser() : items.get(0);
	}
}
