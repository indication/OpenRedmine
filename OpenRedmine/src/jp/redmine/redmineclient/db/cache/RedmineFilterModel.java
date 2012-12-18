package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.url.RemoteUrlIssues;


public class RedmineFilterModel {
	protected Dao<RedmineFilter, Integer> dao;
	public RedmineFilterModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineFilter.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<RedmineFilter> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineFilter> fetchAll(int connection) throws SQLException{
		List<RedmineFilter> item;
		item = dao.queryForEq(RedmineFilter.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineFilter>();
		}
		return item;
	}

	public RedmineFilter fetchByCurrnt(int connection,long project) throws SQLException{
		RedmineFilter item;
		QueryBuilder<RedmineFilter, Integer> builder = dao.queryBuilder();
		Where<RedmineFilter, Integer> where = builder.where()
				.eq(RedmineFilter.CONNECTION, connection)
				.and()
				.eq(RedmineFilter.PROJECT, project)
				.and()
				.eq(RedmineFilter.CURRENT, true)
				;
		builder.setWhere(where);
		Log.d("RedmineFilter",builder.prepareStatementString());
		item = dao.queryForFirst(builder.prepare());
		return item;
	}

	public RedmineFilter generateDefault(int connection,RedmineProject project){
		RedmineFilter item = new RedmineFilter();
		item.setConnectionId(connection);
		item.setProject(project);
		item.setDefault(true);
		item.setFirst(new Date());
		return item;
	}
	public void updateCurrent(RedmineFilter filter) throws SQLException{
		RedmineFilter current = fetchByCurrnt(filter.getConnectionId(),filter.getProjectId());
		if(current != null && current.getId() != filter.getId()){
			current.setCurrent(false);
			dao.update(current);
			filter.setCurrent(true);
		}
		dao.createOrUpdate(filter);
	}

	public static void setupUrl(RemoteUrlIssues url,RedmineFilter filter){
		if(filter.getAssigned() != null)
			url.filterAssigned(String.valueOf(filter.getAssigned().getUserId()));
		if(filter.getAuthor() != null)
			url.filterAuthor(String.valueOf(filter.getAuthor().getUserId()));

		if(filter.getProject() != null)
			url.filterProject(String.valueOf(filter.getProject().getProjectId()));
		if(filter.getTracker() != null)
			url.filterTracker(String.valueOf(filter.getTracker().getTrackerId()));

		if(filter.getPriority() != null)
			url.filterPriority(String.valueOf(filter.getPriority().getPriorityId()));
		if(filter.getCategory() != null)
			url.filterCategory(String.valueOf(filter.getCategory().getCategoryId()));
		if(filter.getVersion() != null)
			url.filterVersion(String.valueOf(filter.getVersion().getVersionId()));
		//@todo
		url.addSort("id", false);

	}

	public RedmineFilter fetchById(int id) throws SQLException{
		RedmineFilter item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineFilter();
		return item;
	}

	public int insert(RedmineFilter item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineFilter item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineFilter item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(int id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}

}
