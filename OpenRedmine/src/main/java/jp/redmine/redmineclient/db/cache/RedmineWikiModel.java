package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineWiki;


public class RedmineWikiModel implements IMasterModel<RedmineWiki> {
	private final static String TAG = RedmineWikiModel.class.getSimpleName();
	protected Dao<RedmineWiki, Long> dao;
	public RedmineWikiModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineWiki.class);
		} catch (SQLException e) {
			Log.e(TAG,"getDao",e);
		}
	}

	public List<RedmineWiki> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineWiki> fetchAll(int connection) throws SQLException{
		List<RedmineWiki> item = dao.queryForEq(RedmineWiki.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineWiki>();
		}
		return item;
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


	@Override
	public long countByProject(int connection_id, long project_id) throws SQLException {
		QueryBuilder<RedmineWiki, ?> builder = dao.queryBuilder();
		builder
			.setCountOf(true)
			.where()
				.eq(RedmineWiki.CONNECTION, connection_id)
				.and()
				.eq(RedmineWiki.PROJECT_ID, project_id)
				;
		return dao.countOf(builder.prepare());
	}

	@Override
	public RedmineWiki fetchItemByProject(int connection_id,
			long project_id, long offset, long limit) throws SQLException {
		QueryBuilder<RedmineWiki, ?> builder = dao.queryBuilder();
		builder
			.limit(limit)
			.offset(offset)
			.orderBy(RedmineWiki.TITLE, true)
			.where()
				.eq(RedmineWiki.CONNECTION, connection_id)
				.and()
				.eq(RedmineWiki.PROJECT_ID, project_id)
				;
		RedmineWiki item = builder.queryForFirst();
		if(item == null)
			item = new RedmineWiki();
		return item;
	}

	public int insert(RedmineWiki item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineWiki item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineWiki item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(long id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
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
