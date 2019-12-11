package jp.redmine.redmineclient.task;

import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineTimeActivityModel;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineIssueRelation;
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
	private final static String TAG = SelectIssueJournalTask.class.getSimpleName();

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
		SelectDataTaskRedmineConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		doInBackgroundIssue(client, params);
		doInBackgroundTimeEntry(client, params);
		return null;
	}

	private void doInBackgroundIssue(SelectDataTaskRedmineConnectionHandler client, Integer... params) {
		final ParserIssue parser = new ParserIssue();
		final List<Integer> listAdditionalIssue = new ArrayList<Integer>();
		DataCreationHandler<RedmineConnection,RedmineIssue> relationHandler = new DataCreationHandler<RedmineConnection,RedmineIssue>() {
			private RedmineIssueModel mRelation = new RedmineIssueModel(helper);
			public void onData(RedmineConnection con,RedmineIssue data) throws SQLException, IOException {
				if(data.getParentId() != 0){
					if(mRelation.getIdByIssue(con.getId(), data.getParentId()) == null)
						listAdditionalIssue.add(data.getParentId());
				}
				if(data.getRelations() == null)
					return;
				for(RedmineIssueRelation rel : data.getRelations()){
					Log.d(TAG,"relation:" + String.valueOf(rel.getIssueId()) + "->" + String.valueOf(rel.getIssueToId()));
					int target_id = rel.getTargetIssueId(data.getIssueId());
					if(mRelation.getIdByIssue(con.getId(), target_id) == null)
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
				helperSetupParserStream(stream, parser);
				parser.parse(connection);
			}
		};
		RemoteUrlIssue url = new RemoteUrlIssue();
		url.setInclude(
				 RemoteUrlIssue.Includes.Journals
				,RemoteUrlIssue.Includes.Relations
				,RemoteUrlIssue.Includes.Attachments
				,RemoteUrlIssue.Includes.Watchers
				);
		for(int param: params){
			url.setIssueId(param);
			fetchData(client, url, handler);
		}
		//Add external issues
		parser.unregisterDataCreation(relationHandler);
		url.setInclude();
		for(int param: listAdditionalIssue){
			url.setIssueId(param);
			fetchData(client, url, handler);
		}
		
	}

	private void doInBackgroundTimeEntry(SelectDataTaskRedmineConnectionHandler client, Integer... params) {
		final RedmineTimeEntryModel model = new RedmineTimeEntryModel(helper);
		final RedmineTimeActivityModel mActivity = new RedmineTimeActivityModel(helper);
		final RedmineUserModel mUser = new RedmineUserModel(helper);
		final ParserTimeEntry parser = new ParserTimeEntry();
		parser.registerDataCreation((con, data) -> {
			data.setConnectionId(con.getId());
			if(data.getActivity() != null){
				data.getActivity().setConnectionId(con.getId());
				mActivity.refreshItem(data);
			}
			mUser.refreshItem(data);
			model.refreshItem(con,data);
		});
		SelectDataTaskDataHandler handler = stream -> {
			helperSetupParserStream(stream,parser);
			parser.parse(connection);
		};

		RemoteUrlTimeEntries url = new RemoteUrlTimeEntries();
		for(int item : params){
			int offset = 0;
			url.filterLimit(LIMIT);
			url.filterIssue(String.valueOf(item));
			do {
				url.filterOffset(offset);
				fetchData(client, url, handler);
				offset += parser.getCount() + 1;
			} while(parser.getCount() == LIMIT);
		}
	}

}
