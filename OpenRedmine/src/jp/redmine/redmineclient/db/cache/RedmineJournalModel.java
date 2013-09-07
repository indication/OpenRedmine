package jp.redmine.redmineclient.db.cache;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedmineJournalChanges;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;


public class RedmineJournalModel {
	private final static String TAG = "RedmineJournalModel";
	protected Dao<RedmineJournal, Long> dao;
	public RedmineJournalModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineJournal.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<RedmineJournal> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineJournal> fetchAll(int connection) throws SQLException{
		List<RedmineJournal> item;
		item = dao.queryForEq(RedmineJournal.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineJournal>();
		}
		return item;
	}

	public RedmineJournal fetchById(int connection, int journalId) throws SQLException{
		PreparedQuery<RedmineJournal> query = dao.queryBuilder().where()
		.eq(RedmineJournal.CONNECTION, connection)
		.and()
		.eq(RedmineJournal.JOURNAL_ID, journalId)
		.prepare();
		Log.d("RedmineProject",query.getStatement());
		RedmineJournal item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineJournal();
		return item;
	}

	public RedmineJournal fetchById(long id) throws SQLException{
		RedmineJournal item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineJournal();
		return item;
	}

	public long countByIssue(int connection_id, long issue_id) throws SQLException {
		QueryBuilder<RedmineJournal, ?> builder = dao.queryBuilder();
		builder
			.setCountOf(true)
			.where()
				.eq(RedmineJournal.CONNECTION, connection_id)
				.and()
				.eq(RedmineJournal.ISSUE_ID, issue_id)
				;
		return dao.countOf(builder.prepare());
	}

	public RedmineJournal fetchItemByIssue(int connection_id, long issue_id,
			long offset, long limit) throws SQLException {
		QueryBuilder<RedmineJournal, ?> builder = dao.queryBuilder();
		builder
			.limit(limit)
			.offset(offset)
			.orderBy(RedmineJournal.JOURNAL_ID, true)
			.where()
				.eq(RedmineJournal.CONNECTION, connection_id)
				.and()
				.eq(RedmineJournal.ISSUE_ID, issue_id)
				;
		RedmineJournal item = builder.queryForFirst();
		if(item == null){
			item = new RedmineJournal();
		} else {
			try {
				item.changes = item.getDetails();
			} catch (IOException e) {
				Log.e(TAG,"getDetails",e);
			} catch (ClassNotFoundException e) {
				Log.e(TAG,"getDetails",e);
			}
		}
		if(item.changes == null){
			item.changes = new ArrayList<RedmineJournalChanges>();
		}
		return item;
	}

	public int insert(RedmineJournal item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineJournal item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineJournal item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(long id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
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
			if(project.getModified().after(data.getModified())){
				this.update(data);
			}
		}
		return data;
	}

	public RedmineJournal refreshItem(RedmineJournal journal) throws SQLException {
		return refreshItem(journal.getConnectionId(), journal);
	}
}
