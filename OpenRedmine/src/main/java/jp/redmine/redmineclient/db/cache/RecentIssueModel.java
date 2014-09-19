package jp.redmine.redmineclient.db.cache;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;

import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineRecentIssue;


public class RecentIssueModel {
	private static final String TAG = RecentIssueModel.class.getSimpleName();
	protected Dao<RedmineRecentIssue, Long> dao;
	public RecentIssueModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineRecentIssue.class);
		} catch (SQLException e) {
			Log.e(TAG,"getDao",e);
		}
	}

	public void ping(RedmineIssue issue) throws SQLException {
		RedmineRecentIssue item = dao.queryForFirst(
			dao.queryBuilder().where()
				.eq(RedmineRecentIssue.CONNECTION,issue.getConnectionId())
				.and()
				.eq(RedmineRecentIssue.ISSUE, issue.getId())
				.prepare()
		);
		if (item == null){
			item = new RedmineRecentIssue();
			item.setIssue(issue);
			item.setProject(issue.getProject());
			item.setConnectionId(issue.getConnectionId());
			item.setCount(0);
			item.setCreated(new Date());
		}
		item.setModified(new Date());
		item.countup();
		dao.createOrUpdate(item);
	}

	public int strip(RedmineProject project, long count) throws SQLException {
		QueryBuilder<RedmineRecentIssue, Long> builder = dao.queryBuilder();
		builder.where()
				.eq(RedmineRecentIssue.CONNECTION, project.getConnectionId())
				.and()
				.eq(RedmineRecentIssue.PROJECT, project.getId())
				;
		builder.offset(count)
				.limit(100L)
				.orderBy(RedmineRecentIssue.MODIFIED, false);
		int deleted = 0;
		for(RedmineRecentIssue item : dao.query(builder.prepare())){
			deleted += dao.delete(item);
		}
		return deleted;
	}
}
