package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineConnection;
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
		.eq(RedmineNews.CONNECTION, connection)
		.and()
		.eq(RedmineNews.NEWS_ID, newsId)
		.prepare();
		Log.d(TAG,query.getStatement());
		List<RedmineNews> items = dao.query(query);
		return items.size() < 1 ? new RedmineNews() : items.get(0);
	}

	public RedmineNews fetchById(long id) throws SQLException{
		RedmineNews item = dao.queryForId(id);
		if(item == null)
			item = new RedmineNews();
		return item;
	}

	public int insert(RedmineNews item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedmineNews item) throws SQLException{
		return dao.update(item);
	}
	public int delete(RedmineNews item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(long id) throws SQLException{
		return dao.deleteById(id);
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
	public RedmineNews refreshItem(RedmineConnection con, RedmineNews news) throws SQLException {
		return refreshItem(con.getId(), news);
	}

	public RedmineNews refreshItem(RedmineNews news) throws SQLException {
		return refreshItem(news.getConnectionId(), news);
	}
}
