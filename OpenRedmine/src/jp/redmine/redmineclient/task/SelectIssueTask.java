package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.parser.IssueModelDataCreationHandler;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssues;

public class SelectIssueTask extends SelectDataTask<RedmineIssue> {

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
	protected List<RedmineIssue> doInBackground(Integer... params) {
		long offset = params[0];
		long limit = params[1];
		boolean isRest = (params.length > 2 && params[2] == 1) ? true : false;
		List<RedmineIssue> issues = null;
		RedmineFilterModel mFilter = new RedmineFilterModel(helper);
		RedmineIssueModel mIssue = new RedmineIssueModel(helper);
		try {
			RedmineFilter filter = mFilter.fetchByCurrnt(connection.getId(), project);
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
				RedmineFilterModel.setupUrl(url, filter);
				url.filterOffset((int)offset);
				url.filterLimit((int)limit);
				fetchData(connection, url, new SelectDataTaskDataHandler<RedmineConnection>() {
					@Override
					public void onContent(RedmineConnection item, InputStream stream)
							throws XmlPullParserException, IOException, SQLException {
						IssueModelDataCreationHandler handler = new IssueModelDataCreationHandler(helper);
						parser.registerDataCreation(handler);
						helperSetupParserStream(stream, parser);
						parser.parse(project);
					}
				});

				filter.setFetched(parser.getCount()+fetched);
				filter.setLast(new Date());
				mFilter.updateCurrent(filter);
			}
			issues = mIssue.fetchAllByFilter(filter,offset,limit);
		} catch (SQLException e) {
			publishError(e);
		}
		if(issues == null)
			issues = new ArrayList<RedmineIssue>();
		return issues;
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
