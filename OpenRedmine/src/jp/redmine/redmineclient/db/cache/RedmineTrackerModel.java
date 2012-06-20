package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTracker;
import android.content.Context;


public class RedmineTrackerModel extends BaseCacheModel<DatabaseCacheHelper> {
	protected Dao<RedmineTracker, Integer> dao;
	public RedmineTrackerModel(Context context) {
		super(context);
		try {
			dao = getHelper().getDao(RedmineTracker.class);
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

	public void refreshItem(RedmineConnection info,RedmineTracker data) throws SQLException{

		RedmineTracker project = this.fetchById(info.getId(), data.getTrackerId());
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
