package jp.redmine.redmineclient.task;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineCategoryModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserCategory;
import jp.redmine.redmineclient.parser.ParserVersion;
import jp.redmine.redmineclient.url.RemoteUrlCategory;
import jp.redmine.redmineclient.url.RemoteUrlVersion;

public class SelectProjectEnumerationTask extends SelectDataTask<Void,Integer> {

	protected DatabaseCacheHelper helper;
	protected RedmineConnection connection;
	protected RedmineProject project;
	public SelectProjectEnumerationTask(DatabaseCacheHelper helper,RedmineConnection con,RedmineProject proj){
		this.helper = helper;
		this.connection = con;
		this.project = proj;
	}


	public SelectProjectEnumerationTask() {
	}

	@Override
	protected Void doInBackground(Integer... params) {
		SelectDataTaskRedmineConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		fetchVersions(client,project);
		fetchCategory(client,project);
		return null;
	}

	protected void fetchVersions(SelectDataTaskRedmineConnectionHandler client,final RedmineProject project){
		final RedmineVersionModel model = new RedmineVersionModel(helper);
		RemoteUrlVersion url = new RemoteUrlVersion();
		url.setProject(project);

		fetchData(client, url, new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserVersion parser = new ParserVersion();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineProjectVersion>() {
					public void onData(RedmineConnection con,RedmineProjectVersion data) throws SQLException {
						data.setConnectionId(con.getId());
						data.setProject(project);
						model.refreshItem(con,data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(connection);
			}
		});
	}
	protected void fetchCategory(SelectDataTaskRedmineConnectionHandler client,final RedmineProject project){
		final RedmineCategoryModel model = new RedmineCategoryModel(helper);
		final RedmineUserModel modelUser = new RedmineUserModel(helper);
		RemoteUrlCategory url = new RemoteUrlCategory();
		url.setProject(project);

		fetchData(client, url, new SelectDataTaskDataHandler() {
			@Override
			public void onContent(InputStream stream)
					throws XmlPullParserException, IOException, SQLException {
				ParserCategory parser = new ParserCategory();
				parser.registerDataCreation(new DataCreationHandler<RedmineConnection,RedmineProjectCategory>() {
					public void onData(RedmineConnection con,RedmineProjectCategory data) throws SQLException {
						data.setConnectionId(con.getId());
						if(data.getAssignTo() != null){
							data.setAssignTo(modelUser.fetchById(con.getId(), data.getAssignTo().getUserId()));
						}
						data.setProject(project);
						model.refreshItem(con,data);
					}
				});
				helperSetupParserStream(stream,parser);
				parser.parse(connection);
			}
		});
	}
}
