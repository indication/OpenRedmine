package jp.redmine.redmineclient.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import jp.redmine.redmineclient.DatabaseHelper;
import jp.redmine.redmineclient.entity.RedmineProject;
import android.content.Context;


public class RedmineProjectModel extends BaseModel<DatabaseHelper> {
	protected Dao<RedmineProject, Integer> dao;
	public RedmineProjectModel(Context context) {
		super(context);
		try {
			dao = getHelper().getDao(RedmineProject.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<RedmineProject> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineProject> fetchAll(int connection) throws SQLException{
		List<RedmineProject> item;
		item = dao.queryForEq(RedmineProject.CONNECTION, connection);
		if(item == null)
			item = new ArrayList<RedmineProject>();
		return item;
	}

	public RedmineProject fetchById(int connection, int projectId) throws SQLException{
		RedmineProject item = dao.queryForFirst(dao.queryBuilder()
				.where()
				.eq(RedmineProject.CONNECTION, connection)
				.eq(RedmineProject.PROJECT_ID, projectId)
				.prepare()
			);
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

}
