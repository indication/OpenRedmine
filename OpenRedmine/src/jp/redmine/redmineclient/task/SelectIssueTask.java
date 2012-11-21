package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

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
	ParserIssue parser = new ParserIssue();
	public SelectIssueTask(DatabaseCacheHelper helper,RedmineConnection con,RedmineProject proj){
		this.helper = helper;
		this.project = proj;
		this.connection = con;
	}


	public SelectIssueTask() {
	}


	@Override
	protected void onContent(RedmineConnection connection, InputStream stream) throws IOException, XmlPullParserException {
		IssueModelDataCreationHandler handler = new IssueModelDataCreationHandler(helper);
		parser.registerDataCreation(handler);
		XmlPullParser xmlPullParser = Xml.newPullParser();
		xmlPullParser.setInput(stream, "UTF-8");
		parser.setXml(xmlPullParser);
		parser.parse(project);
	}

	@Override
	protected List<RedmineIssue> doInBackground(Integer... params) {
		long offset = params[0];
		long limit = params[1];
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
			if((offset+limit) > filter.getFetched())
				isRemote = true;

			if(isRemote){
				RemoteUrlIssues url = new RemoteUrlIssues();
				RedmineFilterModel.setupUrl(url, filter);
				url.filterOffset((int)offset);
				url.filterLimit((int)limit);
				fetchData(connection, url);

				filter.setFetched(parser.getCount()+filter.getFetched());
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
