package jp.redmine.redmineclient.service;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineTimeActivityModel;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserTimeEntry;
import jp.redmine.redmineclient.task.Fetcher;
import jp.redmine.redmineclient.task.SelectDataTaskDataHandler;
import jp.redmine.redmineclient.task.SelectDataTaskRedmineConnectionHandler;
import jp.redmine.redmineclient.url.RemoteUrlTimeEntries;

/**
 * Created by dai on 15/05/07.
 */
public class SyncTimeentry {

	public static boolean fetchByIssue(final DatabaseCacheHelper helper
			, final SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error
			, int issue_id
			, int offset
			, int limit
	) throws SQLException {
		final RedmineTimeEntryModel model = new RedmineTimeEntryModel(helper);
		final RedmineTimeActivityModel mActivity = new RedmineTimeActivityModel(helper);
		final RedmineUserModel mUser = new RedmineUserModel(helper);
		final ParserTimeEntry parser = new ParserTimeEntry();
		parser.registerDataCreation(new DataCreationHandler<RedmineConnection, RedmineTimeEntry>() {
			public void onData(RedmineConnection con, RedmineTimeEntry data) throws SQLException {
				data.setConnectionId(con.getId());
				if (data.getActivity() != null) {
					data.getActivity().setConnectionId(con.getId());
					mActivity.refreshItem(data);
				}
				mUser.refreshItem(data);
				model.refreshItem(con, data);
			}
		});
		final SelectDataTaskDataHandler handler = new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				Fetcher.setupParserStream(stream, parser);
				parser.parse(client.getConnection());
			}
		};

		RemoteUrlTimeEntries url = new RemoteUrlTimeEntries();
		url.filterLimit(limit);
		url.filterIssue(String.valueOf(issue_id));
		url.filterOffset(offset);
		Fetcher.fetchData(client, error, client.getUrl(url), handler);
		return parser.getCount() >= limit;
	}

}
