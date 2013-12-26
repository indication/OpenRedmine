package jp.redmine.redmineclient.task;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.db.cache.RedmineWikiModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.entity.RedmineWiki;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserTimeEntry;
import jp.redmine.redmineclient.parser.ParserWiki;
import jp.redmine.redmineclient.url.RemoteUrlTimeEntries;
import jp.redmine.redmineclient.url.RemoteUrlWiki;

public class SelectWikiTask extends SelectDataTask<Void,String> {
	private final static String TAG = SelectWikiTask.class.getSimpleName();
	private final static int LIMIT = 50;

	protected DatabaseCacheHelper helper;
	protected RedmineConnection connection;
	protected RedmineProject project;
	public SelectWikiTask(DatabaseCacheHelper helper, RedmineConnection con, RedmineProject proj){
		this.helper = helper;
		this.connection = con;
		this.project = proj;
	}
	public SelectWikiTask(DatabaseCacheHelper helper, RedmineConnection con, long proj_id){
		this.helper = helper;
		this.connection = con;
		RedmineProjectModel proj = new RedmineProjectModel(helper);
		try {
			this.project = proj.fetchById(proj_id);
		} catch (SQLException e) {
			Log.e(TAG, "constructor", e);
		}
	}


	public SelectWikiTask() {
	}

	@Override
	protected Void doInBackground(String... params) {
		final RedmineWikiModel model = new RedmineWikiModel(helper);
		final ParserWiki parser = new ParserWiki();
		parser.registerDataCreation(new DataCreationHandler<RedmineProject,RedmineWiki>() {
			public void onData(RedmineProject con,RedmineWiki data) throws SQLException {
				data.setProject(con);
				data.setConnectionId(con.getConnectionId());
				model.refreshItem(con.getConnectionId(), con.getId(),data);
			}
		});
		SelectDataTaskDataHandler handler = new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				helperSetupParserStream(stream,parser);
				parser.parse(project);
			}
		};

		SelectDataTaskConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		RemoteUrlWiki url = new RemoteUrlWiki();
		for(String item : params){
			int offset = 0;
			url.filterLimit(LIMIT);
			url.setProject(project.getIdentifier());
			if(StringUtils.isEmpty(item))
				url.setTitle(RemoteUrlWiki.list);
			else
				url.setTitle(item);

			do {
				url.filterOffset(offset);
				fetchData(client,connection, url, handler);
				offset += parser.getCount() + 1;
			} while(parser.getCount() == LIMIT);
		}
		client.close();
		return null;
	}

	@Override
	protected void onErrorRequest(int statuscode) {

	}

	@Override
	protected void onProgress(int max, int proc) {

	}

}
