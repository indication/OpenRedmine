package jp.redmine.redmineclient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.adapter.RedmineIssueListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.intent.IssueIntent;
import jp.redmine.redmineclient.intent.ProjectIntent;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.task.SelectIssueTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class IssueListActivity extends OrmLiteBaseActivity<DatabaseCacheHelper>
	implements OnScrollListener
	{
	public IssueListActivity(){
		super();
	}

	private ArrayAdapter<RedmineIssue> listAdapter;
	private SelectDataTask task;
	private View mFooter;
	private ListView listView;

	@Override
	protected void onDestroy() {
		cancelTask();
		super.onDestroy();
	}
	protected void cancelTask(){
		// cleanup task
		if(task != null && task.getStatus() == Status.RUNNING){
			task.cancel(true);
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issuelist);


		listView = (ListView)findViewById(R.id.listConnectionList);
		listAdapter = new RedmineIssueListAdapter(
				this,R.layout.issueitem
				,new ArrayList<RedmineIssue>());
		listView.addFooterView(getFooter());
		getFooter().setVisibility(View.INVISIBLE);
		listView.setAdapter(listAdapter);

		listView.setOnScrollListener(this);

		//リスト項目がクリックされた時の処理
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				ListView listView = (ListView) parent;
				Object listitem = listView.getItemAtPosition(position);
				if(listitem == null || ! RedmineIssue.class.isInstance(listitem)  )
				{
					return;
				}
				RedmineIssue item = (RedmineIssue) listitem;
				IssueIntent intent = new IssueIntent(getApplicationContext(), IssueViewActivity.class );
				intent.setConnectionId(item.getConnectionId());
				intent.setIssueId(item.getIssueId());
				startActivity( intent.getIntent() );
			}
		});
	}

	private View getFooter() {
		if (mFooter == null) {
			mFooter = getLayoutInflater()
				.inflate(R.layout.listview_footer,null);
		}
		return mFooter;
	}

	protected void onRefresh(){
		if(task != null && task.getStatus() == Status.RUNNING){
			return;
		}
		listAdapter.notifyDataSetInvalidated();
		listAdapter.clear();
		listAdapter.notifyDataSetChanged();
		task = new SelectDataTask();
		task.execute(0,20);
	}

	private class SelectDataTask extends SelectIssueTask {
		public SelectDataTask() {
			super();
			ProjectIntent intent = new ProjectIntent(getIntent());
			int connectionid = intent.getConnectionId();
			long projectid = intent.getProjectId();
			helper = getHelper();
			try {
				ConnectionModel mConnection = new ConnectionModel(getApplicationContext());
				connection = mConnection.getItem(connectionid);
				mConnection.finalize();
				RedmineProjectModel mProject = new RedmineProjectModel(helper);
				project = mProject.fetchById(projectid);
			} catch (SQLException e) {
				Log.e("IssueListActivity","SelectDataTask",e);
			}
		}
		// can use UI thread here
		@Override
		protected void onPreExecute() {
			getFooter().setVisibility(View.VISIBLE);
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(List<RedmineIssue> issues) {
			helperAddItems(listAdapter, issues);
			getFooter().setVisibility(View.INVISIBLE);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu( menu );

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.projects, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_projects_refresh:
			{
				this.onRefresh();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	public void onScroll(AbsListView view, int firstVisibleItem,
		int visibleItemCount, int totalItemCount) {
		if (totalItemCount == firstVisibleItem + visibleItemCount) {
			if(task != null && task.getStatus() == Status.RUNNING)
				return;
			task = new SelectDataTask();
			task.execute(totalItemCount,10);
		}
	}
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}
}
