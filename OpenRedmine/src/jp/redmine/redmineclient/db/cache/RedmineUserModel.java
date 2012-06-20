package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineUser;


public class RedmineUserModel {
	protected Dao<RedmineUser, Integer> dao;
	public RedmineUserModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineUser.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<RedmineUser> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineUser> fetchAll(int connection) throws SQLException{
		List<RedmineUser> item;
		item = dao.queryForEq(RedmineUser.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineUser>();
		}
		return item;
	}

	public RedmineUser fetchById(int connection, int statusId) throws SQLException{
		PreparedQuery<RedmineUser> query = dao.queryBuilder().where()
		.eq(RedmineUser.CONNECTION, connection)
		.and()
		.eq(RedmineUser.USER_ID, statusId)
		.prepare();
		RedmineUser item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineUser();
		return item;
	}

	public RedmineUser fetchById(int id) throws SQLException{
		RedmineUser item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineUser();
		return item;
	}

	public int insert(RedmineUser item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineUser item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineUser item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(int id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}

	public void refreshItem(RedmineConnection info,RedmineUser data) throws SQLException{

		RedmineUser project = this.fetchById(info.getId(), data.getUserId());
		if(project.getId() == null){
			data.setRedmineConnection(info);
			this.insert(data);
		} else {
			if(project.getModified().after(data.getModified())){
				data.setId(project.getId());
				data.setRedmineConnection(info);
				this.update(data);
			}
		}
	}
}
