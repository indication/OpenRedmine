package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import android.util.Log;


public class RedmineIssueModel {
	protected Dao<RedmineIssue, Integer> dao;

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

	public List<RedmineIssue> fetchAllById(int connection, long projectId, Long startRow, Long maxRows) throws SQLException{
		QueryBuilder<RedmineIssue, Integer> builder = dao.queryBuilder();
		Where<RedmineIssue, Integer> where = builder.where()
		.eq(RedmineIssue.CONNECTION, connection)
		.and()
		.eq(RedmineIssue.PROJECT_ID, projectId)
		;//.prepare();
		builder.setWhere(where);
		return fetchAllBy(builder,startRow,maxRows);
	}
	public List<RedmineIssue> fetchAllByFilter(RedmineFilter filter, Long startRow, Long maxRows) throws SQLException{
		Hashtable<String, Object> dic = new Hashtable<String, Object>();
		if(filter.getConnectionId() != null) dic.put(RedmineFilter.CONNECTION,	filter.getConnectionId());
		if(filter.getProject()	 != null) dic.put(RedmineFilter.PROJECT,		filter.getProject()		);
		if(filter.getTracker()	 != null) dic.put(RedmineFilter.TRACKER,		filter.getTracker()		);
		if(filter.getAssigned()	 != null) dic.put(RedmineFilter.ASSIGNED,		filter.getAssigned()	);
		if(filter.getAuthor()	 != null) dic.put(RedmineFilter.AUTHOR,			filter.getAuthor()		);
		if(filter.getCategory()	 != null) dic.put(RedmineFilter.CATEGORY,		filter.getCategory()	);
		if(filter.getStatus()	 != null) dic.put(RedmineFilter.STATUS,			filter.getStatus()		);
		if(filter.getVersion()	 != null) dic.put(RedmineFilter.VERSION,		filter.getVersion()		);

		QueryBuilder<RedmineIssue, Integer> builder = dao.queryBuilder();
		Where<RedmineIssue, Integer> where = builder.where();
		boolean isFirst = true;
		for(Enumeration<String> e = dic.keys() ; e.hasMoreElements() ;){
			String key = e.nextElement();
			if(dic.get(key) == null)
				continue;
			if(isFirst){
				isFirst = false;
			} else {
				where.and();
			}
			where.eq(key, dic.get(key));
		}
		builder.setWhere(where);
		return fetchAllBy(builder,startRow,maxRows);
	}

	public  List<RedmineIssue> fetchAllBy(QueryBuilder<RedmineIssue, Integer> builder, Long startRow, Long maxRows) throws SQLException{
		if(maxRows != null){
			builder.limit(maxRows);
		}
		if(startRow != null && startRow != 0){
			builder.offset(startRow);
		}
		Log.d("RedmineIssue",builder.prepareStatementString());
		List<RedmineIssue> item = dao.query(builder.prepare());
		if(item == null)
			item = new ArrayList<RedmineIssue>();
		Log.d("RedmineIssue","count:" + item.size());
		return item;
	}

	public RedmineIssue fetchById(int connection, int issueId) throws SQLException{
		PreparedQuery<RedmineIssue> query = dao.queryBuilder().where()
		.eq(RedmineIssue.CONNECTION, connection)
		.and()
		.eq(RedmineIssue.ISSUE_ID, issueId)
		.prepare();
		Log.d("RedmineIssue",query.getStatement());
		RedmineIssue item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineIssue();
		return item;
	}
	public RedmineIssue fetchById(int id) throws SQLException{
		RedmineIssue item;
		item = dao.queryForId(id);
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
	public int delete(int id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}

	public void refreshItem(RedmineConnection info,RedmineIssue data) throws SQLException{

		RedmineIssue project = this.fetchById(info.getId(), data.getIssueId());
		if(project.getId() == null){
			data.setRedmineConnection(info);
			this.insert(data);
		} else {
			if(project.getModified().after(data.getModified())){
				data.setId(project.getId());
				data.setRedmineConnection(info);
				this.update(data);
			}
		}
	}
	public void refreshItem(RedmineProject info,RedmineIssue data) throws SQLException{

		RedmineIssue project = this.fetchById(info.getConnectionId(), data.getIssueId());
		if(project.getId() == null){
			data.setConnectionId(info.getConnectionId());
			data.setProject(info);
			this.insert(data);
		} else {
			if(project.getModified().after(data.getModified())){
				data.setId(project.getId());
				data.setConnectionId(info.getConnectionId());
				data.setProject(info);
				this.update(data);
			}
		}
	}
}
