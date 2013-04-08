package jp.redmine.redmineclient.db.cache;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import android.util.Log;


public class RedmineTimeEntryModel implements IMasterModel<RedmineTimeEntry> {
	protected Dao<RedmineTimeEntry, Integer> dao;
	public RedmineTimeEntryModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineTimeEntry.class);
		} catch (SQLException e) {
			Log.e("RedmineTimeEntryModel","getDao",e);
		}
	}

	public List<RedmineTimeEntry> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineTimeEntry> fetchAll(int connection) throws SQLException{
		List<RedmineTimeEntry> item;
		item = dao.queryForEq(RedmineTimeEntry.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineTimeEntry>();
		}
		return item;
	}

	public RedmineTimeEntry fetchById(int connection, int statusId) throws SQLException{
		PreparedQuery<RedmineTimeEntry> query = dao.queryBuilder().where()
		.eq(RedmineTimeEntry.CONNECTION, connection)
		.and()
		.eq(RedmineTimeEntry.TIMEENTRY_ID, statusId)
		.prepare();
		Log.d("RedmineTimeEntryModel",query.getStatement());
		RedmineTimeEntry item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineTimeEntry();
		return item;
	}

	public RedmineTimeEntry fetchById(int id) throws SQLException{
		RedmineTimeEntry item = dao.queryForId(id);
		if(item == null)
			item = new RedmineTimeEntry();
		return item;
	}

	public BigDecimal sumByIssueId(int connection_id, int issue_id) throws SQLException{
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

	@Override
	public long countByProject(int connection_id, long project_id) throws SQLException {
		QueryBuilder<RedmineTimeEntry, ?> builder = dao.queryBuilder();
		builder
			.setCountOf(true)
			.where()
				.eq(RedmineTimeEntry.CONNECTION, connection_id)
				.and()
				.eq(RedmineTimeEntry.ISSUE_ID, (int)project_id)
				;
		return dao.countOf(builder.prepare());
	}

	@Override
	public RedmineTimeEntry fetchItemByProject(int connection_id,
			long project_id, long offset, long limit) throws SQLException {
		QueryBuilder<RedmineTimeEntry, ?> builder = dao.queryBuilder();
		builder
			.limit(limit)
			.offset(offset)
			//.orderBy(RedmineTimeEntry.NAME, true)
			.where()
				.eq(RedmineTimeEntry.CONNECTION, connection_id)
				.and()
				.eq(RedmineTimeEntry.ISSUE_ID, (int) project_id)
				;
		RedmineTimeEntry item = builder.queryForFirst();
		if(item == null)
			item = new RedmineTimeEntry();
		return item;
	}

	public int insert(RedmineTimeEntry item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineTimeEntry item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineTimeEntry item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(int id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
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
			if(timeentry.getModified().after(data.getModified())){
				this.update(data);
			}
		}

		return timeentry;
	}
}
