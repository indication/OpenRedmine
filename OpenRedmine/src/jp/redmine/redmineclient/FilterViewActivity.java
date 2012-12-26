package jp.redmine.redmineclient;


import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.RedmineIssueFilter;
import jp.redmine.redmineclient.intent.ProjectIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class FilterViewActivity extends OrmLiteBaseActivity<DatabaseCacheHelper>  {
	public FilterViewActivity(){
		super();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	private RedmineIssueFilter form;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issuefilter);

		form = new RedmineIssueFilter();

	}

	@Override
	protected void onStart() {
		ProjectIntent intent = new ProjectIntent(getIntent());
		final int connectionid = intent.getConnectionId();
		final long projectid = intent.getProjectId();
		form.setup(this,getHelper(),connectionid,projectid);
		form.setupEvents();
		form.setFilter(getHelper(), connectionid, projectid);
		form.buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RedmineFilter filter = form.getFilter(null);
				RedmineProject project = new RedmineProject();
				project.setConnectionId(connectionid);
				project.setId(projectid);
				filter.setProject(project);
				filter.setConnectionId(connectionid);

				RedmineFilterModel model = new RedmineFilterModel(getHelper());
				try {
					model.updateSynonym(filter);
				} catch (SQLException e) {
					Log.e("FilterViewActivity","buttonSave.OnClick",e);
				}
				finish();
			}
		});
		super.onStart();
	}
}
