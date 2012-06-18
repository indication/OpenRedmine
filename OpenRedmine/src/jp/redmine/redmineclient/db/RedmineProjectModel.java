package jp.redmine.redmineclient.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import android.content.Context;
import android.util.Log;


public class RedmineProjectModel extends BaseCacheModel<DatabaseCacheHelper> {
	protected Dao<RedmineProject, Integer> dao;
	public RedmineProjectModel(Context context) {
		super(context);
		try {
			dao = getHelper().getDao(RedmineProject.class);
		} catch (SQLException e) {
			Log.e("RedmineProjectModel","getDao",e);
		}
	}

	public List<RedmineProject> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineProject> fetchAll(int connection) throws SQLException{
		List<RedmineProject> item;
		item = dao.queryForEq(RedmineProject.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineProject>();
		}
		return item;
	}

	public RedmineProject fetchById(int connection, int projectId) throws SQLException{
		PreparedQuery<RedmineProject> query = dao.queryBuilder().where()
		.eq(RedmineProject.CONNECTION, connection)
		.and()
		.eq(RedmineProject.PROJECT_ID, projectId)
		.prepare();
		Log.d("RedmineProject",query.getStatement());
		RedmineProject item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineProject();
		return item;
	}

	public RedmineProject fetchById(int id) throws SQLException{
		RedmineProject item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineProject();
		return item;
	}

	public int insert(RedmineProject item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineProject item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineProject item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(int id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}

	public void refreshItem(RedmineConnection info,RedmineProject data) throws SQLException{

		RedmineProject project = this.fetchById(info.getId(), data.getProjectId());
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
