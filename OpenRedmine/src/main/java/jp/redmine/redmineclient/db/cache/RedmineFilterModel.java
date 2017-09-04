package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineProject;


public class RedmineFilterModel {
	private final static String TAG = RedmineFilterModel.class.getSimpleName();
	protected Dao<RedmineFilter, Integer> dao;
	public RedmineFilterModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineFilter.class);
		} catch (SQLException e) {
			Log.e(TAG, "getDao", e);
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

	public RedmineFilter fetchByCurrent(int connection,long project) throws SQLException{
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
		Log.d(TAG,builder.prepareStatementString());
		item = dao.queryForFirst(builder.prepare());
		return item;
	}
	public List<RedmineFilter> fetchAllByConnection(int connection) throws SQLException{
		List<RedmineFilter> list;
		QueryBuilder<RedmineFilter, Integer> builder = dao.queryBuilder();
		Where<RedmineFilter, Integer> where = builder.where()
				.eq(RedmineFilter.CONNECTION, connection)
				;
		builder.setWhere(where);
		Log.d(TAG,builder.prepareStatementString());
		list = dao.query(builder.prepare());
		if(list==null){
			list = new ArrayList<>();
		}
		return list;
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
		RedmineFilter current = fetchByCurrent(filter.getConnectionId(),filter.getProjectId());
		if(current != null && current.getId() != filter.getId()){
			current.setCurrent(false);
			dao.update(current);
		}
		filter.setCurrent(true);
		dao.createOrUpdate(filter);
	}
	public RedmineFilter getSynonym(RedmineFilter filter) throws SQLException{
		Compare<IMasterRecord> compareMaster = new Compare<IMasterRecord>(){
			@Override
			public boolean isSameInner(IMasterRecord a, IMasterRecord b) {
				return a.getId() == b.getId();
			}
		};
		Compare<String> compareString = new Compare<String>(){
			@Override
			public boolean isSameInner(String a, String b) {
				return a.equals(b);
			}
		};
		Compare<Boolean> compareBoolean = new Compare<Boolean>() {
			@Override
			public boolean isSameInner(Boolean a, Boolean b) {
				return a.equals(b);
			}
		};
		for(RedmineFilter item : fetchAllByConnection(filter.getConnectionId())){
			if(!compareMaster.isSame(filter.getProject(), item.getProject()))
				continue;
			if(!compareMaster.isSame(filter.getCategory(), item.getCategory()))
				continue;
			if(!compareMaster.isSame(filter.getVersion(), item.getVersion()))
				continue;
			if(!compareMaster.isSame(filter.getTracker(), item.getTracker()))
				continue;
			if(!compareMaster.isSame(filter.getStatus(), item.getStatus()))
				continue;
			if(!compareMaster.isSame(filter.getPriority(), item.getPriority()))
				continue;
			if(!compareMaster.isSame(filter.getAuthor(), item.getAuthor()))
				continue;
			if(!compareMaster.isSame(filter.getAssigned(), item.getAssigned()))
				continue;
			if(!compareString.isSame(filter.getSort(), item.getSort()))
				continue;
			if(!compareBoolean.isSame(filter.isClosed(), item.isClosed()))
				continue;
			return item;
		}
		return null;
	}
	public void updateSynonym(RedmineFilter filter) throws SQLException{
		RedmineFilter target = getSynonym(filter);
		if(target == null)
			target = filter;
		updateCurrent(target);
	}

	abstract class Compare<T>{
		public abstract boolean isSameInner(T a,T b);
		public boolean isSideNull(T a,T b){
			return (a != null && b == null) || (a == null && b != null);
		}
		public boolean isBothNotNull(T a,T b){
			return a != null && b != null;
		}
		public boolean isBothNull(T a,T b){
			return a == null && b == null;
		}
		public boolean isSame(T a,T b){
			if(isSideNull(a,b))
				return false;
			if(isBothNull(a, b))
				return true;
			if(!isSameInner(a,b))
				return false;
			return true;
		}
	}

	public RedmineFilter fetchById(int id) throws SQLException{
		RedmineFilter item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineFilter();
		return item;
	}

	public int insert(RedmineFilter item) throws SQLException{
		return dao.create(item);
	}

	public int update(RedmineFilter item) throws SQLException{
		return dao.update(item);
	}
	public int updateOrInsert(RedmineFilter item) throws SQLException{
		CreateOrUpdateStatus count = dao.createOrUpdate(item);
		return count.getNumLinesChanged();
	}
	public int delete(RedmineFilter item) throws SQLException{
		return dao.delete(item);
	}
	public int delete(int id) throws SQLException{
		return dao.deleteById(id);
	}

}
