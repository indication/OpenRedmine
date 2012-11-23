package jp.redmine.redmineclient.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineProject;

import android.content.Context;
import android.util.Log;

public class ProjectModel extends Connector {
	private int connection_id;
	public ProjectModel(Context context, int connectionid) {
		super(context);
		connection_id = connectionid;
	}

	public List<RedmineProject> fetchAllData(){
		final RedmineProjectModel model =
			new RedmineProjectModel(helperCache);
		List<RedmineProject> projects = new ArrayList<RedmineProject>();
		try {
			projects = model.fetchAll(connection_id);
		} catch (SQLException e) {
			Log.e("SelectDataTask","doInBackground",e);
		} catch (Throwable e) {
			Log.e("SelectDataTask","doInBackground",e);
		}
		return projects;
	}

}
