package jp.redmine.redmineclient.db.store;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import jp.redmine.redmineclient.entity.RedmineConnection;


public class RedmineConnectionModel {
	protected Dao<RedmineConnection, Integer> dao;
	public RedmineConnectionModel(DatabaseHelper helper) {
		try {
			dao = helper.getDao(RedmineConnection.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<RedmineConnection> fetchAll() throws SQLException{
		return dao.queryForAll();
	}



	public RedmineConnection fetchById(int id) throws SQLException{
		RedmineConnection item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineConnection();
		return item;
	}

	public int update(RedmineConnection item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineConnection item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(int id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}

	public int create(RedmineConnection item) throws SQLException {
		return dao.create(item);
	}

	public long countByProject(Integer connection_id, int projectid) throws SQLException {
		QueryBuilder<RedmineConnection, ?> builder = dao.queryBuilder();
		builder
			.setCountOf(true)
				;
		return dao.countOf(builder.prepare());
	}

	public RedmineConnection fetchItemByProject(Integer connection_id, int projectid,long offset, long limit) throws SQLException {
		QueryBuilder<RedmineConnection, Integer> builder = dao.queryBuilder();
		setupLimit(builder,offset,limit);
		builder.orderBy(RedmineConnection.ID, true);
		RedmineConnection item = dao.queryForFirst(builder.prepare());
		if(item == null)
			item = new RedmineConnection();
		return item;
	}
	protected void setupLimit(QueryBuilder<?,?> builder,Long startRow, Long maxRows) throws SQLException{
		if(maxRows != null){
			builder.limit(maxRows);
		}
		if(startRow != null && startRow != 0){
			builder.offset(startRow);
		}
	}

}
