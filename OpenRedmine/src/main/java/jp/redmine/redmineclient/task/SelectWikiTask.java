package jp.redmine.redmineclient.task;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineAttachmentModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.db.cache.RedmineWikiModel;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineWiki;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserWiki;
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
		final RedmineAttachmentModel mAttachment = new RedmineAttachmentModel(helper);
		final RedmineUserModel mUser = new RedmineUserModel(helper);
		final ParserWiki parser = new ParserWiki();
		parser.registerDataCreation(new DataCreationHandler<RedmineProject,RedmineWiki>() {
			public void onData(RedmineProject con,RedmineWiki data) throws SQLException {
				data.setProject(con);
				data.setConnectionId(con.getConnectionId());
				data = model.refreshItem(con.getConnectionId(), con.getId(),data);
				for (RedmineAttachment attachment : data.getAttachments()){
					onDataAttachment(data,attachment);
				}
			}
			protected void onDataAttachment(RedmineWiki data, RedmineAttachment attachment) throws SQLException {
				attachment.setConnectionId(data.getConnectionId());
				attachment.setWikiId(data.getId());
				mUser.refreshItem(attachment);
				mAttachment.refreshItem(attachment);
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

		SelectDataTaskRedmineConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		RemoteUrlWiki url = new RemoteUrlWiki();
		url.setInclude(RemoteUrlWiki.Includes.Attachments);
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
				fetchData(client, url, handler);
				offset += parser.getCount() + 1;
			} while(parser.getCount() == LIMIT);
		}
		return null;
	}
}
