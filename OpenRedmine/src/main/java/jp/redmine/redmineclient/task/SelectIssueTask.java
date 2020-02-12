package jp.redmine.redmineclient.task;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.parser.IssueModelDataCreationHandler;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssues;

public class SelectIssueTask extends SelectDataTask<Void,Integer> {

	protected DatabaseCacheHelper helper;
	protected Long project_id;
	protected Integer filter_id;
	protected RedmineConnection connection;
	public SelectIssueTask(DatabaseCacheHelper helper,RedmineConnection con,long proj){
		this.helper = helper;
		this.project_id = proj;
		this.connection = con;
	}
	public SelectIssueTask(DatabaseCacheHelper helper,RedmineConnection con,Long proj,int filter){
		this.helper = helper;
		this.filter_id = filter;
		this.connection = con;
	}


	public SelectIssueTask() {
	}


	protected RedmineFilter getFilter(RedmineFilterModel mFilter){

		RedmineFilter filter = null;
		try {
			if(filter_id == null){
				RedmineProjectModel mProject = new RedmineProjectModel(helper);
				RedmineProject project = mProject.fetchById(project_id);
				filter = mFilter.fetchByCurrent(connection.getId(), project.getId());
				if(filter == null){
					filter = mFilter.generateDefault(connection.getId(), project);
					filter.setCurrent(true);
				}
			} else {
				filter = mFilter.fetchById(filter_id);
			}
		} catch (SQLException e) {
			publishError(e);
		}
		return filter;
	}
	protected void updateFilter(RedmineFilterModel mFilter, RedmineFilter filter){
		try {
			mFilter.updateOrInsert(filter);
		} catch (SQLException e) {
			publishError(e);
		}

	}
	@Override
	protected Void doInBackground(Integer... params) {
		long limit = params[1];
		boolean isRest = (params.length > 2 && params[2] == 1);
		RedmineFilterModel mFilter = new RedmineFilterModel(helper);
		RedmineFilter filter = getFilter(mFilter);
		if(filter == null)
			return null;

		/*
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 10);	///@todo
		if(filter.getFirst() == null || cal.after(filter.getFirst()))
			filter.setFetched(0);
			*/
		long lastfetched = filter.getFetched();
		long fetched = 0;

		//accelerate fetch issues
		if(isRest){
			if(lastfetched <= 0){
				lastfetched = limit;
			}
			if(lastfetched > (limit*2))
				limit *= 2;
		} else {
			fetched = lastfetched;
			lastfetched += limit;
		}

		SelectDataTaskRedmineConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		final ParserIssue parser = new ParserIssue();
		SelectDataTaskDataHandler taskhandler = stream -> {
			IssueModelDataCreationHandler handler = new IssueModelDataCreationHandler(helper);
			parser.registerDataCreation(handler);
			helperSetupParserStream(stream, parser);
			parser.parse(connection);
		};
		RemoteUrlIssues url = new RemoteUrlIssues();
		RemoteUrlIssues.setupFilter(url, filter, filter.isClosed() == null ? true : filter.isClosed());
		try {
			boolean isFirst = true;
			while(fetched < lastfetched){
				url.filterOffset((int)fetched);
				url.filterLimit((int)limit);
				fetchData(client, url, taskhandler);

				// update fetch status
				fetched += parser.getCount();
				if(parser.getCount() < 1)
					break;

				//update offset for next fetch
				fetched++;
				if(isFirst){
					//sleep for server
					Thread.sleep(1000);
					isFirst = false;
				}
			}
			fetched--;
			filter.setFetched(fetched);
			filter.setLast(new Date());
			updateFilter(mFilter,filter);
		} catch (InterruptedException e) {
			publishError(e);
		}

		return null;
	}

}
