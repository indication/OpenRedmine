package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProject;


public class RedmineProjectModel{
	private final static String TAG = RedmineProjectModel.class.getSimpleName();
	protected Dao<RedmineProject, Long> dao;
	public RedmineProjectModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineProject.class);
		} catch (SQLException e) {
			Log.e(TAG,"getDao",e);
		}
	}

	public List<RedmineProject> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineProject> fetchAll(int connection) throws SQLException{
		List<RedmineProject> item;
		item = dao.queryForEq(RedmineProject.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<>();
		}
		return item;
	}

	public RedmineProject fetchById(int connection, int projectId) throws SQLException{
		PreparedQuery<RedmineProject> query = dao.queryBuilder().where()
		.eq(RedmineProject.CONNECTION, connection)
		.and()
		.eq(RedmineProject.PROJECT_ID, projectId)
		.prepare();
		Log.d(TAG,query.getStatement());
		List<RedmineProject> items = dao.query(query);
		return items.size() < 1 ? new RedmineProject() : items.get(0);
	}

	public RedmineProject fetchById(long id) throws SQLException{
		RedmineProject item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineProject();
		return item;
	}

	public int insert(RedmineProject item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedmineProject item) throws SQLException{
		return dao.update(item);
	}
	public int delete(RedmineProject item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(long id) throws SQLException{
		return dao.deleteById(id);
	}
	public void refreshItem(RedmineConnection info,RedmineIssue data) throws SQLException{
		data.setProject(refreshItem(info,data.getProject()));
	}

	public RedmineProject refreshItem(RedmineConnection info,RedmineProject data) throws SQLException{

		RedmineProject project = this.fetchById(info.getId(), data.getProjectId());
		data.setRedmineConnection(info);
		if(project.getId() == null){
			this.insert(data);
			data = fetchById(info.getId(), data.getProjectId());
		} else {
			data.setId(project.getId());
			data.setFavorite(project.getFavorite());
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

}
