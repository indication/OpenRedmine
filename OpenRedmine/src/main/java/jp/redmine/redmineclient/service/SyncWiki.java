package jp.redmine.redmineclient.service;

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
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineWiki;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserWiki;
import jp.redmine.redmineclient.task.Fetcher;
import jp.redmine.redmineclient.task.SelectDataTaskDataHandler;
import jp.redmine.redmineclient.task.SelectDataTaskRedmineConnectionHandler;
import jp.redmine.redmineclient.url.RemoteUrlWiki;

public class SyncWiki {

	public static boolean fetchWiki(final DatabaseCacheHelper helper
			, final SelectDataTaskRedmineConnectionHandler client
			, Fetcher.ContentResponseErrorHandler error
			, long project_id
			, String name
			, int offset
			, int limit
	) throws SQLException {
		final RedmineWikiModel model = new RedmineWikiModel(helper);
		final RedmineAttachmentModel mAttachment = new RedmineAttachmentModel(helper);
		final RedmineUserModel mUser = new RedmineUserModel(helper);
		final RedmineProjectModel mProject = new RedmineProjectModel(helper);
		final ParserWiki parser = new ParserWiki();
		final RedmineProject project = mProject.fetchById(project_id);
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
				Fetcher.setupParserStream(stream, parser);
				parser.parse(project);
			}
		};


		RemoteUrlWiki url = new RemoteUrlWiki();
		url.setInclude(RemoteUrlWiki.Includes.Attachments);
		url.filterLimit(limit);
		url.setProject(project.getIdentifier());
		if(StringUtils.isEmpty(name))
			url.setTitle(RemoteUrlWiki.list);
		else
			url.setTitle(name);

		url.filterOffset(offset);
		Fetcher.fetchData(client, error, client.getUrl(url), handler);
		return parser.getCount() >= limit;
	}
}
