package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;


public class RedmineIssueModel {
	protected Dao<RedmineIssue, Long> dao;

	public RedmineIssueModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineIssue.class);
		} catch (SQLException e) {
			Log.e("RedmineIssueModel","getDao",e);
		}
	}

	public List<RedmineIssue> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineIssue> fetchAll(int connection) throws SQLException{
		List<RedmineIssue> item;
		item = dao.queryForEq(RedmineIssue.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineIssue>();
		}
		return item;
	}
	
	protected QueryBuilder<RedmineIssue, Long> builderByProject(int connection_id, long project_id) throws SQLException{
		QueryBuilder<RedmineIssue, Long> builder = dao.queryBuilder();
		builder.where()
			.eq(RedmineIssue.CONNECTION, connection_id)
			.and()
			.eq(RedmineIssue.PROJECT_ID, project_id)
			;
		return builder;
	}
	protected QueryBuilder<RedmineIssue, Long> builderByIssue(int connection_id, long issue_id) throws SQLException{
		QueryBuilder<RedmineIssue, Long> builder = dao.queryBuilder();
		builder.where()
			.eq(RedmineIssue.CONNECTION, connection_id)
			.and()
			.eq(RedmineIssue.ISSUE_ID, issue_id)
			;
		return builder;
	}

	public long countByProject(int connection_id, long project_id) throws SQLException {
		QueryBuilder<RedmineIssue, ?> builder = builderByProject(connection_id,project_id);
		return dao.countOf(builder.setCountOf(true).prepare());
	}
	public RedmineIssue fetchItemByProject(int connection, long projectId, Long startRow, Long maxRows) throws SQLException{
		QueryBuilder<RedmineIssue, Long> builder = builderByProject(connection,projectId);
		builder.orderBy(RedmineIssue.ISSUE_ID, true);
		setupLimit(builder,startRow,maxRows);

		return fetchBy(builder);
	}

	protected void setupLimit(QueryBuilder<?,?> builder,Long startRow, Long maxRows) throws SQLException{
		if(maxRows != null){
			builder.limit(maxRows);
		}
		if(startRow != null && startRow != 0){
			builder.offset(startRow);
		}
	}
	protected RedmineIssue fetchBy(QueryBuilder<RedmineIssue, Long> builder) throws SQLException{
		Log.d("RedmineIssue",builder.prepareStatementString());
		RedmineIssue item = dao.queryForFirst(builder.prepare());
		if(item == null)
			item = new RedmineIssue();
		return item;
	}

	protected List<RedmineIssue> fetchAllBy(QueryBuilder<RedmineIssue, Long> builder) throws SQLException{
		Log.d("RedmineIssue",builder.prepareStatementString());
		List<RedmineIssue> item = dao.query(builder.prepare());
		if(item == null)
			item = new ArrayList<RedmineIssue>();
		Log.d("RedmineIssue","count:" + item.size());
		return item;
	}

	public RedmineIssue fetchById(int connection, int issueId) throws SQLException{
		PreparedQuery<RedmineIssue> query = builderByIssue(connection,issueId).prepare();
		Log.d("RedmineIssue",query.getStatement());
		RedmineIssue item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineIssue();
		return item;
	}
	
	public Long getIdByIssue(int connection, int issueId) throws SQLException{
		QueryBuilder<RedmineIssue, Long> builder = builderByIssue(connection,issueId);

		builder.selectRaw(RedmineIssue.ID);
		GenericRawResults<String[]> result = builder.queryRaw();
		String[] values = result.getFirstResult();
		result.close();
		return (values != null && values[0] != null) ? Long.parseLong(values[0]) : null;
	}
	
	public RedmineIssue fetchById(long id) throws SQLException{
		RedmineIssue item = dao.queryForId(id);
		if(item == null)
			item = new RedmineIssue();
		return item;
	}

	public int insert(RedmineIssue item) throws SQLException{
		Log.d("RedmineIssue","insert");
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineIssue item) throws SQLException{
		Log.d("RedmineIssue","update");
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineIssue item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(long id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}
	public void refreshItem(RedmineConnection info,RedmineIssue data) throws SQLException{
		refreshItem(info.getId(),data);
	}

	public void refreshItem(int connection_id,RedmineIssue data) throws SQLException{

		RedmineIssue issue = this.fetchById(connection_id, data.getIssueId());
		data.setConnectionId(connection_id);
		if(issue.getId() == null){
			this.insert(data);
			issue = this.fetchById(connection_id, data.getIssueId());
			data.setId(issue.getId());
		} else {
			data.setId(issue.getId());
			if(!data.getModified().before(issue.getModified())){
				this.update(data);
			}
		}
	}
	public void refreshItem(RedmineProject info,RedmineIssue data) throws SQLException{
		data.setProject(info);
		refreshItem(info.getConnectionId(),data);
	}

}
