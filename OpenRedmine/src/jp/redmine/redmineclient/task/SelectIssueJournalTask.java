package jp.redmine.redmineclient.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineTimeActivityModel;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.IssueModelDataCreationHandler;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.parser.ParserTimeEntry;
import jp.redmine.redmineclient.url.RemoteUrlIssue;
import jp.redmine.redmineclient.url.RemoteUrlTimeEntries;

public class SelectIssueJournalTask extends SelectDataTask<Void,Integer> {
	private final static int LIMIT = 50;

	protected DatabaseCacheHelper helper;
	protected RedmineConnection connection;
	public SelectIssueJournalTask(DatabaseCacheHelper helper,RedmineConnection con,RedmineProject proj){
		this.helper = helper;
		this.connection = con;
	}


	public SelectIssueJournalTask() {
	}

	@Override
	protected Void doInBackground(Integer... params) {
		SelectDataTaskConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		doInBackgroundIssue(client, params);
		doInBackgroundTimeEntry(client, params);
		client.close();
		return null;
	}

	protected void doInBackgroundIssue(SelectDataTaskConnectionHandler client,Integer... params) {
		final ParserIssue parser = new ParserIssue();
		SelectDataTaskDataHandler handler = new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				IssueModelDataCreationHandler handler = new IssueModelDataCreationHandler(helper);
				parser.registerDataCreation(handler);
				helperSetupParserStream(stream, parser);
				parser.parse(connection);
			}
		};
		RemoteUrlIssue url = new RemoteUrlIssue();
		url.setInclude(RemoteUrlIssue.Includes.Journals,RemoteUrlIssue.Includes.Relations);
		for(int param: params){
			url.setIssueId(param);
			fetchData(client,connection, url, handler);
		}
	}

	protected void doInBackgroundTimeEntry(SelectDataTaskConnectionHandler client,Integer... params) {
		final RedmineTimeEntryModel model = new RedmineTimeEntryModel(helper);
		final RedmineTimeActivityModel mActivity = new RedmineTimeActivityModel(helper);
		final RedmineUserModel mUser = new RedmineUserModel(helper);
		final ParserTimeEntry parser = new ParserTimeEntry();
		parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineTimeEntry>() {
			public void onData(RedmineConnection con,RedmineTimeEntry data) throws SQLException {
				data.setConnectionId(con.getId());
				if(data.getActivity() != null){
					data.getActivity().setConnectionId(con.getId());
					mActivity.refreshItem(data);
				}
				mUser.refreshItem(data);
				model.refreshItem(con,data);
			}
		});
		SelectDataTaskDataHandler handler = new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				helperSetupParserStream(stream,parser);
				parser.parse(connection);
			}
		};

		RemoteUrlTimeEntries url = new RemoteUrlTimeEntries();
		for(int item : params){
			int offset = 0;
			url.filterLimit(LIMIT);
			url.filterIssue(String.valueOf(item));
			do {
				url.filterOffset(offset);
				fetchData(client,connection, url, handler);
				offset += parser.getCount() + 1;
			} while(parser.getCount() == LIMIT);
		}
	}

	@Override
	protected void onErrorRequest(int statuscode) {

	}

	@Override
	protected void onProgress(int max, int proc) {

	}

}
