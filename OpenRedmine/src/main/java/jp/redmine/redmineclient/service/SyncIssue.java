package jp.redmine.redmineclient.service;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.parser.IssueModelDataCreationHandler;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.task.Fetcher;
import jp.redmine.redmineclient.task.SelectDataTaskDataHandler;
import jp.redmine.redmineclient.task.SelectDataTaskRedmineConnectionHandler;
import jp.redmine.redmineclient.url.RemoteUrlIssues;

public class SyncIssue {

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
		return fetchIssues(helper, client, error, mFilter, filter, offset, limit, isFetchAll);
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
		RemoteUrlIssues url = new RemoteUrlIssues();
		RemoteUrlIssues.setupFilter(url, filter, isFetchAll);

		url.filterOffset(offset);
		url.filterLimit(limit);
		Fetcher.fetchData(client, error, client.getUrl(url), taskhandler);

		filter.setFetched(offset+limit);
		filter.setLast(new Date());
		mFilter.updateOrInsert(filter);
		return parser.getCount() >= limit;
	}

}
