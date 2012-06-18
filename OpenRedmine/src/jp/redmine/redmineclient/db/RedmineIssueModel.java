package jp.redmine.redmineclient.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import android.content.Context;
import android.util.Log;


public class RedmineIssueModel extends BaseCacheModel<DatabaseCacheHelper> {
	protected Dao<RedmineIssue, Integer> dao;
	public RedmineIssueModel(Context context) {
		super(context);
		try {
			dao = getHelper().getDao(RedmineIssue.class);
		} catch (SQLException e) {
			Log.e("RedmineIssueModel","getDao",e);
		}
	}

	public List<RedmineIssue> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineIssue> fetchAll(int connection) throws SQLException{
		List<RedmineIssue> item;
		item = dao.queryForEq(RedmineIssue.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineIssue>();
		}
		return item;
	}

	public List<RedmineIssue> fetchAllById(int connection, int projectId) throws SQLException{
		PreparedQuery<RedmineIssue> query = dao.queryBuilder().where()
		.eq(RedmineIssue.CONNECTION, connection)
		.and()
		.eq(RedmineIssue.PROJECT_ID, projectId)
		.prepare();
		Log.d("RedmineIssue",query.getStatement());
		List<RedmineIssue> item = dao.query(query);
		if(item == null)
			item = new ArrayList<RedmineIssue>();
		Log.d("RedmineIssue","count:" + item.size());
		return item;
	}

	public RedmineIssue fetchById(int connection, int issueId) throws SQLException{
		PreparedQuery<RedmineIssue> query = dao.queryBuilder().where()
		.eq(RedmineIssue.CONNECTION, connection)
		.and()
		.eq(RedmineIssue.ISSUE_ID, issueId)
		.prepare();
		Log.d("RedmineIssue",query.getStatement());
		RedmineIssue item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineIssue();
		return item;
	}
	public RedmineIssue fetchById(int id) throws SQLException{
		RedmineIssue item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineIssue();
		return item;
	}

	public int insert(RedmineIssue item) throws SQLException{
		Log.d("RedmineIssue","insert");
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineIssue item) throws SQLException{
		Log.d("RedmineIssue","update");
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineIssue item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(int id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}

	public void refreshItem(RedmineConnection info,RedmineIssue data) throws SQLException{

		RedmineIssue project = this.fetchById(info.getId(), data.getIssueId());
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
	public void refreshItem(RedmineProject info,RedmineIssue data) throws SQLException{

		RedmineIssue project = this.fetchById(info.getConnectionId(), data.getIssueId());
		if(project.getId() == null){
			data.setConnectionId(info.getConnectionId());
			data.setProject(info);
			this.insert(data);
		} else {
			if(project.getModified().after(data.getModified())){
				data.setId(project.getId());
				data.setConnectionId(info.getConnectionId());
				data.setProject(info);
				this.update(data);
			}
		}
	}
}
