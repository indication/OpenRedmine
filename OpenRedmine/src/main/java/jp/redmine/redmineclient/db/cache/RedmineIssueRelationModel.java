package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineIssueRelation;


public class RedmineIssueRelationModel {
	private final static String TAG = RedmineIssueRelationModel.class.getSimpleName();
	protected Dao<RedmineIssueRelation, Long> dao;
	public RedmineIssueRelationModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineIssueRelation.class);
		} catch (SQLException e) {
			Log.e(TAG, "getDao", e);
		}
	}

	public List<RedmineIssueRelation> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineIssueRelation> fetchAll(int connection) throws SQLException{
		List<RedmineIssueRelation> item;
		item = dao.queryForEq(RedmineIssueRelation.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<>();
		}
		return item;
	}

	public RedmineIssueRelation fetchById(int connection, int journalId) throws SQLException{
		PreparedQuery<RedmineIssueRelation> query = dao.queryBuilder().where()
		.eq(RedmineIssueRelation.CONNECTION, connection)
		.and()
		.eq(RedmineIssueRelation.RELATION_ID, journalId)
		.prepare();
		List<RedmineIssueRelation> items = dao.query(query);
		return items.size() < 1 ? new RedmineIssueRelation() : items.get(0);
	}

	public RedmineIssueRelation fetchById(long id) throws SQLException{
		RedmineIssueRelation item = dao.queryForId(id);
		if(item == null)
			item = new RedmineIssueRelation();
		return item;
	}

	public long countByIssue(int connection_id, long issue_id) throws SQLException {
		QueryBuilder<RedmineIssueRelation, Long> builder = dao.queryBuilder();
		builder
			.setCountOf(true)
			.where()
				.eq(RedmineIssueRelation.ISSUE_ID,	issue_id)
				.or()
				.eq(RedmineIssueRelation.ISSUE_TO_ID,	issue_id)
				.and()
				.eq(RedmineIssueRelation.CONNECTION, connection_id)
				;
		return dao.countOf(builder.prepare());
	}
	public List<RedmineIssueRelation> fetchByIssue(int connection_id, long issue_id, Long offset, Long limit) throws SQLException {
		QueryBuilder<RedmineIssueRelation, Long> builder = dao.queryBuilder();
		if(offset != null && limit != null)
			builder
				.limit(limit)
				.offset(offset);
		builder
			.orderBy(RedmineIssueRelation.RELATION_ID, true)
			.where()
				.eq(RedmineIssueRelation.ISSUE_ID,	issue_id)
				.or()
				.eq(RedmineIssueRelation.ISSUE_TO_ID,	issue_id)
				.and()
				.eq(RedmineIssueRelation.CONNECTION, connection_id)
				;
		List<RedmineIssueRelation> item = builder.query();
		if(item == null){
			item = new ArrayList<>();
		}
		return item;
	}

	public RedmineIssueRelation fetchItemByIssue(int connection_id, long issue_id, long offset, long limit) throws SQLException {
		QueryBuilder<RedmineIssueRelation, Long> builder = dao.queryBuilder();
		builder
			.limit(limit)
			.offset(offset)
			.orderBy(RedmineIssueRelation.RELATION_ID, true)
			.where()
				.eq(RedmineIssueRelation.ISSUE_ID,	issue_id)
				.or()
				.eq(RedmineIssueRelation.ISSUE_TO_ID,	issue_id)
				.and()
				.eq(RedmineIssueRelation.CONNECTION, connection_id)
				;
		Log.d(TAG,builder.prepareStatementString());
		List<RedmineIssueRelation> items = builder.query();
		return items.size() < 1 ? new RedmineIssueRelation() : items.get(0);
	}

	public int insert(RedmineIssueRelation item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedmineIssueRelation item) throws SQLException{
		return dao.update(item);
	}
	public int delete(RedmineIssueRelation item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(long id) throws SQLException{
		return dao.deleteById(id);
	}

	public RedmineIssueRelation refreshItem(int connection_id,RedmineIssueRelation data) throws SQLException{
		if(data == null)
			return null;

		RedmineIssueRelation project = this.fetchById(connection_id, data.getRelationId());
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
			this.update(data);
		}
		return data;
	}

	public RedmineIssueRelation refreshItem(RedmineIssueRelation journal) throws SQLException {
		return refreshItem(journal.getConnectionId(), journal);
	}
}
