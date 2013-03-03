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
	public SelectIssueTask(DatabaseCacheHelper helper,RedmineConnection con,RedmineProject proj){
		this.helper = helper;
		this.project = proj;
		this.connection = con;
	}


	public SelectIssueTask() {
	}

	@Override
	protected Void doInBackground(Integer... params) {
		long offset = params[0];
		long limit = params[1];
		boolean isRest = (params.length > 2 && params[2] == 1) ? true : false;
		RedmineFilterModel mFilter = new RedmineFilterModel(helper);
		try {
			RedmineFilter filter = mFilter.fetchByCurrent(connection.getId(), project.getId());
			if(filter == null)
				filter = mFilter.generateDefault(connection.getId(), project);

			boolean isRemote = false;
			/*
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, 10);	///@todo
			if(filter.getFirst() == null || cal.after(filter.getFirst()))
				filter.setFetched(0);
				*/
			long fetched = (isRest) ? 0 : filter.getFetched();
			if((offset+limit) > fetched)
				isRemote = true;

			if(isRemote){
				final ParserIssue parser = new ParserIssue();
				RemoteUrlIssues url = new RemoteUrlIssues();
				RemoteUrlIssues.setupFilter(url, filter);
				url.filterOffset((int)offset);
				url.filterLimit((int)limit);
				SelectDataTaskConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
				fetchData(client,connection, url, new SelectDataTaskDataHandler() {
					@Override
					public void onContent(InputStream stream)
							throws XmlPullParserException, IOException, SQLException {
						IssueModelDataCreationHandler handler = new IssueModelDataCreationHandler(helper);
						parser.registerDataCreation(handler);
						helperSetupParserStream(stream, parser);
						parser.parse(connection);
					}
				});
				client.close();

				filter.setFetched(parser.getCount()+fetched);
				filter.setLast(new Date());
				mFilter.updateCurrent(filter);
			}
		} catch (SQLException e) {
			publishError(e);
		}
		return null;
	}

	@Override
	protected void onErrorRequest(int statuscode) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void onProgress(int max, int proc) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
