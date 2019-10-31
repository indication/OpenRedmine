package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;


public class RedmineTimeEntryModel {
	private final static String TAG = RedmineTimeEntryModel.class.getSimpleName();
	protected Dao<RedmineTimeEntry, Integer> dao;
	public RedmineTimeEntryModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineTimeEntry.class);
		} catch (SQLException e) {
			Log.e(TAG,"getDao",e);
		}
	}

	public List<RedmineTimeEntry> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineTimeEntry> fetchAll(int connection) throws SQLException{
		List<RedmineTimeEntry> item;
		item = dao.queryForEq(RedmineTimeEntry.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<>();
		}
		return item;
	}

	public RedmineTimeEntry fetchById(int connection, int statusId) throws SQLException{
		PreparedQuery<RedmineTimeEntry> query = dao.queryBuilder().where()
		.eq(RedmineTimeEntry.CONNECTION, connection)
		.and()
		.eq(RedmineTimeEntry.TIMEENTRY_ID, statusId)
		.prepare();
		Log.d(TAG,query.getStatement());
		List<RedmineTimeEntry> items = dao.query(query);
		return items.size() < 1 ? new RedmineTimeEntry() : items.get(0);
	}

	public RedmineTimeEntry fetchById(int id) throws SQLException{
		RedmineTimeEntry item = dao.queryForId(id);
		if(item == null)
			item = new RedmineTimeEntry();
		return item;
	}

	public BigDecimal sumByIssueId(int connection_id, long issue_id) throws SQLException{
		QueryBuilder<RedmineTimeEntry, ?> builder = dao.queryBuilder();
		builder
			.where()
				.eq(RedmineTimeEntry.CONNECTION, connection_id)
				.and()
				.eq(RedmineTimeEntry.ISSUE_ID, issue_id)
				;
		BigDecimal result = new BigDecimal(0);
		for(RedmineTimeEntry ent : builder.query()){
			result = result.add(ent.getHours());
		}
		return result;
	}

	public int insert(RedmineTimeEntry item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedmineTimeEntry item) throws SQLException{
		return dao.update(item);
	}
	public int delete(RedmineTimeEntry item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(int id) throws SQLException{
		return dao.deleteById(id);
	}

	public RedmineTimeEntry refreshItem(RedmineConnection info,RedmineTimeEntry data) throws SQLException{
		return refreshItem(info.getId(),data);
	}
	public RedmineTimeEntry refreshItem(int connection_id,RedmineTimeEntry data) throws SQLException{
		if(data == null)
			return null;

		data.setConnectionId(connection_id);
		RedmineTimeEntry timeentry = this.fetchById(connection_id, data.getTimeentryId());
		if(timeentry.getId() == null){
			this.insert(data);
			timeentry = fetchById(connection_id, data.getTimeentryId());
		} else {
			data.setId(timeentry.getId());
			if(timeentry.getModified() == null){
				timeentry.setModified(new java.util.Date());
			}
			if(data.getModified() == null){
				data.setModified(new java.util.Date());
			}
			if(!timeentry.getModified().before(data.getModified())){
				this.update(data);
			}
		}

		return timeentry;
	}
}
