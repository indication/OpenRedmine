package jp.redmine.redmineclient.fragment;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.FilterViewActivity;
import jp.redmine.redmineclient.IssueEditActivity;
import jp.redmine.redmineclient.IssueListActivity;
import jp.redmine.redmineclient.IssueViewActivity;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.RedmineIssueListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.intent.IssueIntent;
import jp.redmine.redmineclient.intent.ProjectIntent;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.task.SelectIssueTask;
import jp.redmine.redmineclient.task.SelectProjectEnumerationTask;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class IssueList extends OrmLiteListFragment<DatabaseCacheHelper> {
	private static final int ACTIVITY_FILTER = 2001;
	private static final int ACTIVITY_EDIT = 2010;
	private RedmineIssueListAdapter adapter;
	private SelectDataTask task;
	private MenuItem menu_refresh;
	private View mFooter;
	private long lastPos = -1;

	public IssueList(){
		super();
	}

	@Override
	public void onDestroy() {
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().addFooterView(mFooter);

		adapter = new RedmineIssueListAdapter(getHelper());
		setListAdapter(adapter);

		getListView().setFastScrollEnabled(true);

		getListView().setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
				if (totalItemCount == firstVisibleItem + visibleItemCount) {
					if(task != null && task.getStatus() == Status.RUNNING)
						return;
					if(lastPos == totalItemCount)
						return;
					onRefresh(false);
					lastPos = totalItemCount;
				}
			}
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {

			}
		});

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3) {
				ListView listView = (ListView) parent;
				Object listitem = listView.getItemAtPosition(position);
				if(listitem == null || ! RedmineIssue.class.isInstance(listitem)  )
				{
					return false;
				}
				RedmineIssue item = (RedmineIssue) listitem;
				IssueIntent intent = new IssueIntent(getActivity(), IssueEditActivity.class );
				intent.setConnectionId(item.getConnectionId());
				intent.setProjectId(item.getProject().getId());
				intent.setIssueId(item.getIssueId());
				startActivity( intent.getIntent() );
				return true;
			}
		});

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFooter = inflater.inflate(R.layout.listview_footer,null);
		mFooter.setVisibility(View.GONE);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		if(adapter != null){
			ProjectIntent intent = new ProjectIntent( getActivity().getIntent() );
			adapter.setupParameter(intent.getConnectionId(),intent.getProjectId());
			adapter.notifyDataSetInvalidated();
			adapter.notifyDataSetChanged();
			if(adapter.getCount() < 1){
				this.onRefresh(true);
			}
		}
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		super.onListItemClick(parent, v, position, id);
		ListView listView = (ListView) parent;
		Object listitem = listView.getItemAtPosition(position);
		if(listitem == null || ! RedmineIssue.class.isInstance(listitem)  )
		{
			return;
		}
		RedmineIssue item = (RedmineIssue) listitem;
		IssueIntent intent = new IssueIntent(getActivity(), IssueViewActivity.class );
		intent.setConnectionId(item.getConnectionId());
		intent.setProjectId(item.getProject().getId());
		intent.setIssueId(item.getIssueId());
		startActivity( intent.getIntent() );
	}

	protected void onItemSelect(RedmineProject item) {
		ProjectIntent intent = new ProjectIntent( getActivity().getApplicationContext(), IssueListActivity.class );
		intent.setConnectionId(item.getConnectionId());
		intent.setProjectId(item.getId());
		startActivity( intent.getIntent() );
	}

	protected void onRefresh(boolean isFlush){
		if(task != null && task.getStatus() == Status.RUNNING){
			return;
		}
		adapter.notifyDataSetInvalidated();
		adapter.notifyDataSetChanged();
		if(lastPos != getListView().getChildCount()){
			lastPos = -1; //reset
		}

		ProjectIntent intent = new ProjectIntent(getActivity().getIntent());
		DatabaseCacheHelper helper = getHelper();
		RedmineConnection connection = null;
		RedmineProject project = null;
		try {
			ConnectionModel mConnection = new ConnectionModel(getActivity());
			connection = mConnection.getItem(intent.getConnectionId());
			mConnection.finalize();
			RedmineProjectModel mProject = new RedmineProjectModel(helper);
			project = mProject.fetchById(intent.getProjectId());
		} catch (SQLException e) {
			Log.e("IssueListActivity","SelectDataTask",e);
		}

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		task = new SelectDataTask(helper,connection,project);
		task.setFetchAll(sp.getBoolean("issue_get_all", false));
		task.execute(0,10,isFlush ? 1 : 0);
		if(isFlush){
			SelectProjectEnumerationTask enumtask = new SelectProjectEnumerationTask(helper,connection,project);
			enumtask.execute();
		}
	}

	private class SelectDataTask extends SelectIssueTask {
		public SelectDataTask(DatabaseCacheHelper helper,RedmineConnection connection, RedmineProject project) {
			super(helper,connection,project);
		}

		// can use UI thread here
		@Override
		protected void onPreExecute() {
			mFooter.setVisibility(View.VISIBLE);
			if(menu_refresh != null)
				menu_refresh.setEnabled(false);
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(Void b) {
			mFooter.setVisibility(View.GONE);
			adapter.notifyDataSetInvalidated();
			adapter.notifyDataSetChanged();
			if(menu_refresh != null)
				menu_refresh.setEnabled(true);
		}

		@Override
		protected void onProgress(int max, int proc) {
			adapter.notifyDataSetInvalidated();
			adapter.notifyDataSetChanged();
			super.onProgress(max, proc);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate( R.menu.issues, menu );
		menu_refresh = menu.findItem(R.id.menu_refresh);
		if(task != null && task.getStatus() == Status.RUNNING)
			menu_refresh.setEnabled(false);
		super.onCreateOptionsMenu(menu, inflater);
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
				ProjectIntent intent = new ProjectIntent( getActivity().getIntent() );
				ProjectIntent send = new ProjectIntent( getActivity(), FilterViewActivity.class );
				send.setConnectionId(intent.getConnectionId());
				send.setProjectId(intent.getProjectId());
				startActivityForResult(send.getIntent(), ACTIVITY_FILTER);
				return true;
			}
			case R.id.menu_access_addnew:
			{
				ProjectIntent intent = new ProjectIntent( getActivity().getIntent() );
				ProjectIntent send = new ProjectIntent( getActivity(), IssueEditActivity.class );
				send.setConnectionId(intent.getConnectionId());
				send.setProjectId(intent.getProjectId());
				startActivityForResult(send.getIntent(), ACTIVITY_EDIT);
				return true;
			}

		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case ACTIVITY_FILTER:
			if(resultCode != Activity.RESULT_OK )
				break;
			this.onRefresh(false);
			break;
		default:
			break;
		}
	}

}
