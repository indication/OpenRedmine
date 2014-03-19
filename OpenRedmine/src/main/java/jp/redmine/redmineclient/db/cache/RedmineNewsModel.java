package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.Calendar;

import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineNews;


public class RedmineNewsModel {
	private final static String TAG = RedmineNewsModel.class.getSimpleName();
	protected Dao<RedmineNews, Long> dao;
	public RedmineNewsModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineNews.class);
		} catch (SQLException e) {
			Log.e(TAG,TAG,e);
		}
	}

	public RedmineNews fetchById(int connection, int newsId) throws SQLException{
		PreparedQuery<RedmineNews> query = dao.queryBuilder().where()
		.eq(RedmineAttachment.CONNECTION, connection)
		.and()
		.eq(RedmineAttachment.ATTACHMENT_ID, newsId)
		.prepare();
		Log.d(TAG,query.getStatement());
		RedmineNews item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineNews();
		return item;
	}

	public RedmineNews fetchById(long id) throws SQLException{
		RedmineNews item = dao.queryForId(id);
		if(item == null)
			item = new RedmineNews();
		return item;
	}

	public int insert(RedmineNews item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineNews item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineNews item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(long id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}

	public RedmineNews refreshItem(int connection_id,RedmineNews data) throws SQLException{
		if(data == null)
			return null;

		RedmineNews news = this.fetchById(connection_id, data.getNewsId());
		data.setConnectionId(connection_id);
		data.setDataModified(Calendar.getInstance().getTime());

		if(news.getId() == null){
			this.insert(data);
		} else {
			data.setId(news.getId());

			if(news.getModified() == null){
				news.setModified(new java.util.Date());
			}
			if(data.getModified() == null){
				data.setModified(new java.util.Date());
			}
			this.update(data);
		}
		return data;
	}

	public RedmineNews refreshItem(RedmineNews news) throws SQLException {
		return refreshItem(news.getConnectionId(), news);
	}
}
