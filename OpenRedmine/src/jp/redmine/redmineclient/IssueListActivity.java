package jp.redmine.redmineclient;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.adapter.RedmineIssueListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineBaseAdapterListFormHelper;
import jp.redmine.redmineclient.intent.IssueIntent;
import jp.redmine.redmineclient.intent.ProjectIntent;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.task.SelectIssueTask;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;

public class IssueListActivity extends OrmLiteBaseActivity<DatabaseCacheHelper>
	{
	public IssueListActivity(){
		super();
	}

	private static final int ACTIVITY_FILTER = 2001;
	private SelectDataTask task;
	private long lastPos = 0;
	private RedmineBaseAdapterListFormHelper<RedmineIssueListAdapter> formList;

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
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		formList.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		formList.onRestoreInstanceState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issuelist);

		formList = new RedmineBaseAdapterListFormHelper<RedmineIssueListAdapter>();
		formList.setList((ListView)findViewById(R.id.listConnectionList));
		formList.setFooter(getLayoutInflater().inflate(R.layout.listview_footer,null), false);
		formList.setAdapter(new RedmineIssueListAdapter(getHelper()));
		formList.onRestoreInstanceState(savedInstanceState);

		formList.list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
				if (totalItemCount == firstVisibleItem + visibleItemCount) {
					if(task != null && task.getStatus() == Status.RUNNING)
						return;
					if(lastPos == totalItemCount)
						return;
					task = new SelectDataTask();
					task.execute(totalItemCount,20);
					lastPos = totalItemCount;
				}
			}
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {

			}
		});

		//リスト項目がクリックされた時の処理
		formList.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

	@Override
	protected void onStart() {
		super.onStart();

		ProjectIntent intent = new ProjectIntent( getIntent() );
		formList.adapter.setupParameter(intent.getConnectionId(),intent.getProjectId());
		this.onRefresh(false);
	}

	protected void onRefresh(boolean isFlush){
		if(task != null && task.getStatus() == Status.RUNNING){
			return;
		}
		formList.refresh();
		if(lastPos != formList.list.getChildCount()){
			lastPos = 0; //reset
		}
		task = new SelectDataTask();
		task.execute(0,10,isFlush ? 1 : 0);
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
			formList.setFooterViewVisible(true);
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(Void v) {
			formList.refresh();
			formList.setFooterViewVisible(false);
		}

		@Override
		protected void onError(Exception lasterror) {
			Toast.makeText(getApplicationContext(),
					"Something wrong with program.", Toast.LENGTH_SHORT).show();
			super.onError(lasterror);
		}

		@Override
		protected void onErrorRequest(int statuscode) {
			String message = "Something wrong with the connection.";
			switch(statuscode){
			case 404:
				message = "Something with wrong with connection. Try later.";
				lastPos = 0;
				break;
			case 403:
				message = "Something wrong with connection settings.";
				break;
			case 500:
			case 503:
				message = "Something wrong with server. Please check access by browser.";
				break;
			}
			Toast.makeText(getApplicationContext(),
					message, Toast.LENGTH_SHORT).show();
			super.onErrorRequest(statuscode);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu( menu );

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.issues, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_refresh:
			{
				this.onRefresh(true);
				return true;
			}
			case R.id.menu_issues_filter:
			{
				ProjectIntent intent = new ProjectIntent( getIntent() );
				ProjectIntent send = new ProjectIntent( getApplicationContext(), FilterViewActivity.class );
				send.setConnectionId(intent.getConnectionId());
				send.setProjectId(intent.getProjectId());
				startActivityForResult(send.getIntent(), ACTIVITY_FILTER);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case ACTIVITY_FILTER:
			if(resultCode !=RESULT_OK )
				break;
			this.onRefresh(false);
			break;
		default:
			break;
		}
	}
}
