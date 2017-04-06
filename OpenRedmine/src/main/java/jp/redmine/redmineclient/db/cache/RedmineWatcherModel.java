package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.RedmineWatcher;


public class RedmineWatcherModel {
	private final static String TAG = RedmineWatcherModel.class.getSimpleName();
	protected Dao<RedmineWatcher, Long> dao;
	public RedmineWatcherModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineWatcher.class);
		} catch (SQLException e) {
			Log.e(TAG,TAG,e);
		}
	}

	public RedmineWatcher fetchById(int connection, int issueId, RedmineUser user) throws SQLException{
		PreparedQuery<RedmineWatcher> query = dao.queryBuilder().where()
		.eq(RedmineWatcher.CONNECTION, connection)
		.and()
		.eq(RedmineWatcher.ISSUE_ID, issueId)
		.and()
		.eq(RedmineWatcher.USER_ID, user.getId())
		.prepare();
		RedmineWatcher item = dao.queryForFirst(query);
		if(item == null) {
			item = new RedmineWatcher();
			item.setConnectionId(connection);
			item.setIssueId(issueId);
			item.setUser(user);
		}
		return item;
	}

	public List<RedmineWatcher> fetchByIssue(int connection, int issueId) throws SQLException{
		PreparedQuery<RedmineWatcher> query = dao.queryBuilder().where()
				.eq(RedmineWatcher.CONNECTION, connection)
				.and()
				.eq(RedmineWatcher.ISSUE_ID, issueId)
				.prepare();
		List<RedmineWatcher> items = dao.query(query);
		return items;
	}

	public RedmineWatcher fetchById(long id) throws SQLException{
		RedmineWatcher item = dao.queryForId(id);
		if(item == null)
			item = new RedmineWatcher();
		return item;
	}

	public int insert(RedmineWatcher item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedmineWatcher item) throws SQLException{
		return dao.update(item);
	}
	public int delete(RedmineWatcher item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(long id) throws SQLException{
		return dao.deleteById(id);
	}

	public RedmineWatcher refreshItem(int connection_id,RedmineWatcher data) throws SQLException{
		if(data == null)
			return null;

		RedmineWatcher project = this.fetchById(connection_id, data.getIssueId(), data.getUser());
		data.setConnectionId(connection_id);

		if(project.getId() == null){
			this.insert(data);
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
		return data;
	}

	public RedmineWatcher refreshItem(RedmineWatcher journal) throws SQLException {
		return refreshItem(journal.getConnectionId(), journal);
	}
}
