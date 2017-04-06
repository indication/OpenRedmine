package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineJournal;


public class RedmineJournalModel {
	private final static String TAG = RedmineJournalModel.class.getSimpleName();
	protected Dao<RedmineJournal, Long> dao;
	public RedmineJournalModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineJournal.class);
		} catch (SQLException e) {
			Log.e(TAG, "getDao", e);
		}
	}

	public List<RedmineJournal> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineJournal> fetchAll(int connection) throws SQLException{
		List<RedmineJournal> item;
		item = dao.queryForEq(RedmineJournal.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<>();
		}
		return item;
	}

	public RedmineJournal fetchById(int connection, int journalId) throws SQLException{
		PreparedQuery<RedmineJournal> query = dao.queryBuilder().where()
		.eq(RedmineJournal.CONNECTION, connection)
		.and()
		.eq(RedmineJournal.JOURNAL_ID, journalId)
		.prepare();
		List<RedmineJournal> items = dao.query(query);
		return items.size() < 1 ? new RedmineJournal() : items.get(0);
	}

	public RedmineJournal fetchById(long id) throws SQLException{
		RedmineJournal item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineJournal();
		return item;
	}

	public int insert(RedmineJournal item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedmineJournal item) throws SQLException{
		return dao.update(item);
	}
	public int delete(RedmineJournal item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(long id) throws SQLException{
		return dao.deleteById(id);
	}

	public RedmineJournal refreshItem(int connection_id,RedmineJournal data) throws SQLException{
		if(data == null)
			return null;

		RedmineJournal project = this.fetchById(connection_id, data.getJournalId());
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

	public RedmineJournal refreshItem(RedmineJournal journal) throws SQLException {
		return refreshItem(journal.getConnectionId(), journal);
	}
}
