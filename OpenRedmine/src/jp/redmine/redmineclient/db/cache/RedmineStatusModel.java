package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineStatus;
import android.content.Context;
import android.util.Log;


public class RedmineStatusModel extends BaseCacheModel<DatabaseCacheHelper> {
	protected Dao<RedmineStatus, Integer> dao;
	public RedmineStatusModel(Context context) {
		super(context);
		try {
			dao = getHelper().getDao(RedmineStatus.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<RedmineStatus> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineStatus> fetchAll(int connection) throws SQLException{
		List<RedmineStatus> item;
		item = dao.queryForEq(RedmineStatus.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineStatus>();
		}
		return item;
	}

	public RedmineStatus fetchById(int connection, int statusId) throws SQLException{
		PreparedQuery<RedmineStatus> query = dao.queryBuilder().where()
		.eq(RedmineStatus.CONNECTION, connection)
		.and()
		.eq(RedmineStatus.STATUS_ID, statusId)
		.prepare();
		Log.d("RedmineProject",query.getStatement());
		RedmineStatus item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineStatus();
		return item;
	}

	public RedmineStatus fetchById(int id) throws SQLException{
		RedmineStatus item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineStatus();
		return item;
	}

	public int insert(RedmineStatus item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineStatus item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineStatus item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(int id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}

	public void refreshItem(RedmineConnection info,RedmineStatus data) throws SQLException{

		RedmineStatus project = this.fetchById(info.getId(), data.getStatusId());
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
