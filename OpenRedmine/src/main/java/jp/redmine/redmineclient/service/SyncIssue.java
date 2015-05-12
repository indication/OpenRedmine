package jp.redmine.redmineclient.service;

import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineIssueRelation;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.IssueModelDataCreationHandler;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.task.Fetcher;
import jp.redmine.redmineclient.task.SelectDataTaskDataHandler;
import jp.redmine.redmineclient.task.SelectDataTaskRedmineConnectionHandler;
import jp.redmine.redmineclient.url.RemoteUrlIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssues;

public class SyncIssue {
	private final static String TAG = SyncIssue.class.getSimpleName();

	public static boolean fetchIssuesByProject(final DatabaseCacheHelper helper
			, final SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error
			, long project_id
			, int offset
			, int limit
			, boolean isFetchAll
	) throws SQLException {
		RedmineFilterModel mFilter = new RedmineFilterModel(helper);
		RedmineFilter filter = getFilter(helper, client, mFilter, project_id);
		if(offset == ExecuteMethod.REFRESH_ALL){
			filter.setFetched(0);
			offset = 0;
		}
		return fetchIssues(helper, client, error, mFilter, filter, offset, limit, isFetchAll);
	}

	public static boolean fetchIssuesByFilter(final DatabaseCacheHelper helper
			, final SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error
			, int filter_id
			, int offset
			, int limit
			, boolean isFetchAll
	) throws SQLException {
		RedmineFilterModel mFilter = new RedmineFilterModel(helper);
		RedmineFilter filter = mFilter.fetchById(filter_id);
		if(offset == ExecuteMethod.REFRESH_ALL){
			filter.setFetched(0);
			offset = 0;
		}
		return fetchIssues(helper, client, error, mFilter, filter, offset, limit, isFetchAll);
	}

	private static RedmineFilter getFilter(DatabaseCacheHelper helper
			, SelectDataTaskRedmineConnectionHandler client
			, RedmineFilterModel mFilter
			, long project_id
	) throws SQLException {
		RedmineProjectModel mProject = new RedmineProjectModel(helper);
		RedmineProject project = mProject.fetchById(project_id);
		RedmineFilter filter = mFilter.fetchByCurrent(client.getConnection().getId(), project.getId());
		if(filter == null){
			filter = mFilter.generateDefault(client.getConnection().getId(), project);
			filter.setCurrent(true);
		}
		return filter;
	}

	private static boolean fetchIssues(final DatabaseCacheHelper helper
			, final SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error
		  	, RedmineFilterModel mFilter
			, RedmineFilter filter
			, int offset
			, int limit
			, boolean isFetchAll
	) throws SQLException {
		final ParserIssue parser = new ParserIssue();
		final IssueModelDataCreationHandler handler = new IssueModelDataCreationHandler(helper);
		parser.registerDataCreation(handler);
		SelectDataTaskDataHandler taskhandler = new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				Fetcher.setupParserStream(stream, parser);
				parser.parse(client.getConnection());
			}
		};
		int fetch_offset = offset+(int)(filter.getFetched());
		RemoteUrlIssues url = new RemoteUrlIssues();
		RemoteUrlIssues.setupFilter(url, filter, isFetchAll);

		url.filterOffset(fetch_offset);
		url.filterLimit(limit);
		Fetcher.fetchData(client, error, client.getUrl(url), taskhandler);

		//On finished loading, record the filter
		if(parser.getCount() >= limit && (fetch_offset + limit) <= filter.getFetched()){
			return true;
		} else {
			filter.setFetched(offset + limit);
			filter.setLast(new Date());
			mFilter.updateOrInsert(filter);
			return false;
		}
	}

	public static boolean fetchIssue(final DatabaseCacheHelper helper
			, final SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error
			, int issue_id
	) throws SQLException {
		final ParserIssue parser = new ParserIssue();
		final List<Integer> listAdditionalIssue = new ArrayList<Integer>();
		DataCreationHandler<RedmineConnection,RedmineIssue> relationHandler = new DataCreationHandler<RedmineConnection,RedmineIssue>() {
			private RedmineIssueModel mRelation = new RedmineIssueModel(helper);
			public void onData(RedmineConnection con,RedmineIssue data) throws SQLException {
				if(data.getParentId() != 0){
					if(mRelation.getIdByIssue(con.getId(), data.getParentId()) == null)
						listAdditionalIssue.add(data.getParentId());
				}
				if(data.getRelations() == null)
					return;
				for(RedmineIssueRelation rel : data.getRelations()) {
					Log.d(TAG, "relation:" + String.valueOf(rel.getIssueId()) + "->" + String.valueOf(rel.getIssueToId()));
					int target_id = rel.getTargetIssueId(data.getIssueId());
					if (mRelation.getIdByIssue(con.getId(), target_id) == null)
						listAdditionalIssue.add(target_id);
				}
			}
		};
		IssueModelDataCreationHandler itemhandler = new IssueModelDataCreationHandler(helper);
		parser.registerDataCreation(itemhandler);
		parser.registerDataCreation(relationHandler);

		SelectDataTaskDataHandler handler = new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				Fetcher.setupParserStream(stream, parser);
				parser.parse(client.getConnection());
			}
		};
		RemoteUrlIssue url = new RemoteUrlIssue();
		url.setInclude(
				RemoteUrlIssue.Includes.Journals
				,RemoteUrlIssue.Includes.Relations
				,RemoteUrlIssue.Includes.Attachments
				,RemoteUrlIssue.Includes.Watchers
		);
		url.setIssueId(issue_id);
		Fetcher.fetchData(client, error, client.getUrl(url), handler);
		//Add external issues
		parser.unregisterDataCreation(relationHandler);
		url.setInclude();
		for(int param: listAdditionalIssue){
			url.setIssueId(param);
			Fetcher.fetchData(client, error, client.getUrl(url), handler);
		}
		return false;
	}

}
