package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineIssueRelation;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;


public class RedmineIssueRelationModel {
	private final static String TAG = RedmineIssueRelationModel.class.getSimpleName();
	protected Dao<RedmineIssueRelation, Long> dao;
	public RedmineIssueRelationModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineIssueRelation.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<RedmineIssueRelation> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineIssueRelation> fetchAll(int connection) throws SQLException{
		List<RedmineIssueRelation> item;
		item = dao.queryForEq(RedmineIssueRelation.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineIssueRelation>();
		}
		return item;
	}

	public RedmineIssueRelation fetchById(int connection, int journalId) throws SQLException{
		PreparedQuery<RedmineIssueRelation> query = dao.queryBuilder().where()
		.eq(RedmineIssueRelation.CONNECTION, connection)
		.and()
		.eq(RedmineIssueRelation.RELATION_ID, journalId)
		.prepare();
		RedmineIssueRelation item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineIssueRelation();
		return item;
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
		RedmineIssueRelation item = builder.queryForFirst();
		if(item == null){
			item = new RedmineIssueRelation();
		}
		return item;
	}

	public int insert(RedmineIssueRelation item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineIssueRelation item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineIssueRelation item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(long id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
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
