package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.parser.IssueModelDataCreationHandler;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssues;

public class SelectIssueTask extends SelectDataTask<Void> {

	protected DatabaseCacheHelper helper;
	protected RedmineProject project;
	protected RedmineConnection connection;
	protected boolean isFetchAll = false;
	public SelectIssueTask(DatabaseCacheHelper helper,RedmineConnection con,RedmineProject proj){
		this.helper = helper;
		this.project = proj;
		this.connection = con;
	}


	public SelectIssueTask() {
	}

	@Override
	protected Void doInBackground(Integer... params) {
		long limit = params[1];
		boolean isRest = (params.length > 2 && params[2] == 1) ? true : false;
		RedmineFilterModel mFilter = new RedmineFilterModel(helper);
		RedmineFilter filter = null;
		try {
			filter = mFilter.fetchByCurrent(connection.getId(), project.getId());
		} catch (SQLException e) {
			publishError(e);
		}
		if(filter == null)
			filter = mFilter.generateDefault(connection.getId(), project);

		/*
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 10);	///@todo
		if(filter.getFirst() == null || cal.after(filter.getFirst()))
			filter.setFetched(0);
			*/
		long lastfetched = filter.getFetched();
		long fetched = (isRest) ? 0 : lastfetched;

		//accelerate fetch issues
		if(isRest && lastfetched > (limit*2))
			limit *= 2;

		SelectDataTaskConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		final ParserIssue parser = new ParserIssue();
		SelectDataTaskDataHandler taskhandler = new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				IssueModelDataCreationHandler handler = new IssueModelDataCreationHandler(helper);
				parser.registerDataCreation(handler);
				helperSetupParserStream(stream, parser);
				parser.parse(connection);
			}
		};
		RemoteUrlIssues url = new RemoteUrlIssues();
		RemoteUrlIssues.setupFilter(url, filter, isFetchAll);

		try {
			while(fetched < lastfetched){
				url.filterOffset((int)fetched);
				url.filterLimit((int)limit);
				fetchData(client,connection, url, taskhandler);

				// update fetch status
				fetched += parser.getCount();
				filter.setFetched(fetched);
				filter.setLast(new Date());
				mFilter.updateCurrent(filter);
				if(parser.getCount() < 1)
					break;

				//update offset for next fetch
				fetched++;

				//sleep for server
				Thread.sleep(1000);
			}
		} catch (SQLException e) {
			publishError(e);
		} catch (InterruptedException e) {
			publishError(e);
		} finally {
			client.close();
		}

		return null;
	}

	@Override
	protected void onErrorRequest(int statuscode) {

	}

	@Override
	protected void onProgress(int max, int proc) {

	}

}
