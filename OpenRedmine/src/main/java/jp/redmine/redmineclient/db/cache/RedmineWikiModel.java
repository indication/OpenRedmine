package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.Calendar;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineWiki;


public class RedmineWikiModel {
	private final static String TAG = RedmineWikiModel.class.getSimpleName();
	protected Dao<RedmineWiki, Long> dao;
	public RedmineWikiModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineWiki.class);
		} catch (SQLException e) {
			Log.e(TAG,"getDao",e);
		}
	}

	public RedmineWiki fetchById(int connection, long project_id, String title) throws SQLException{
		PreparedQuery<RedmineWiki> query = dao.queryBuilder().where()
			.eq(RedmineWiki.CONNECTION, connection)
			.and()
			.eq(RedmineWiki.PROJECT_ID, project_id)
			.and()
			.eq(RedmineWiki.TITLE, title)
			.prepare();
		Log.d(TAG,query.getStatement());
		RedmineWiki item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineWiki();
		return item;
	}

	public RedmineWiki fetchById(long id) throws SQLException{
		RedmineWiki item = dao.queryForId(id);
		if(item == null)
			item = new RedmineWiki();
		return item;
	}

	public int insert(RedmineWiki item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedmineWiki item) throws SQLException{
		return dao.update(item);
	}
	public int delete(RedmineWiki item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(long id) throws SQLException{
		return dao.deleteById(id);
	}

	public RedmineWiki refreshItem(RedmineConnection info, long project_id, RedmineWiki data) throws SQLException{
		return refreshItem(info.getId(),project_id,data);
	}
	public RedmineWiki refreshItem(int connection_id,long project_id,RedmineWiki data) throws SQLException{
		if(data == null)
			return null;
		Calendar now = Calendar.getInstance();
		RedmineWiki project = this.fetchById(connection_id, project_id, data.getTitle());
		data.setConnectionId(connection_id);
		data.setDataModified(now.getTime());
		if(project.getId() == null){
			this.insert(data);
			project = fetchById(connection_id, project_id, data.getTitle());
		} else {
			data.setId(project.getId());
			if(project.getModified() == null){
				project.setModified(new java.util.Date());
			}
			if(data.getModified() == null){
				data.setModified(new java.util.Date());
			}
			this.update(data);
		}

		return project;
	}
}
