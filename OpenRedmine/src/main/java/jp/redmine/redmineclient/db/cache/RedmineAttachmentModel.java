package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;

import jp.redmine.redmineclient.entity.RedmineAttachment;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;


public class RedmineAttachmentModel {
	private final static String TAG = RedmineAttachmentModel.class.getSimpleName();
	protected Dao<RedmineAttachment, Long> dao;
	public RedmineAttachmentModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineAttachment.class);
		} catch (SQLException e) {
			Log.e(TAG,TAG,e);
		}
	}

	public RedmineAttachment fetchById(int connection, int journalId) throws SQLException{
		PreparedQuery<RedmineAttachment> query = dao.queryBuilder().where()
		.eq(RedmineAttachment.CONNECTION, connection)
		.and()
		.eq(RedmineAttachment.ATTACHMENT_ID, journalId)
		.prepare();
		Log.d(TAG,query.getStatement());
		RedmineAttachment item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineAttachment();
		return item;
	}

	public RedmineAttachment fetchById(long id) throws SQLException{
		RedmineAttachment item = dao.queryForId(id);
		if(item == null)
			item = new RedmineAttachment();
		return item;
	}

	public int insert(RedmineAttachment item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineAttachment item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineAttachment item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(long id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}

	public RedmineAttachment refreshItem(int connection_id,RedmineAttachment data) throws SQLException{
		if(data == null)
			return null;

		RedmineAttachment project = this.fetchById(connection_id, data.getAttachmentId());
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

	public RedmineAttachment refreshItem(RedmineAttachment journal) throws SQLException {
		return refreshItem(journal.getConnectionId(), journal);
	}
}
