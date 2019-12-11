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
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.IssueModelDataCreationHandler;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssue;

public class SelectIssuePost extends SelectDataPost<List<RedmineIssue>,RedmineIssue> {
	private final static String TAG = "SelectIssuePost";
	protected DatabaseCacheHelper helper;
	protected RedmineConnection connection;
	public SelectIssuePost(DatabaseCacheHelper helper,RedmineConnection con){
		this.helper = helper;
		this.connection = con;
	}


	public SelectIssuePost() {
	}

	@Override
	protected List<RedmineIssue> doInBackground(RedmineIssue... params) {
		final ParserIssue parser = new ParserIssue();
		final List<RedmineIssue> list =  new ArrayList<RedmineIssue>();
		SelectDataTaskDataHandler handler = new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				IssueModelDataCreationHandler handler = new IssueModelDataCreationHandler(helper);
				parser.registerDataCreation(handler);
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection, RedmineIssue>() {
					@Override
					public void onData(RedmineConnection info, RedmineIssue data) throws SQLException {
						list.add(data);
					}
				});

				helperSetupParserStream(stream, parser);
				parser.parse(connection);
			}
		};

		SelectDataTaskRedmineConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		RedmineIssueModel mIssue = new RedmineIssueModel(helper);
		RemoteUrlIssue url = new RemoteUrlIssue();
		for(final RedmineIssue item : params) {
			SelectDataTaskPutHandler puthandler = getPutHandler(item);
			if (item.getIssueId() == null) {
				url.setIssueId((Integer) null);

				postData(client, url, handler, puthandler);
			} else {
				url.setIssueId(item.getIssueId());
				boolean isSuccess = putData(client, url, handler, puthandler);
				if (isSuccess && parser.getCount() < 1) {
					try {
						mIssue.refreshItem(connection, item);
						list.add(item);
					} catch (SQLException e) {
						Log.e(TAG, "update issue", e);
					}
				}
			}
		}
		return list;
	}


}
