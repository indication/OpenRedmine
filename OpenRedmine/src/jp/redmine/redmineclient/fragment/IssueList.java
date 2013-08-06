package jp.redmine.redmineclient.fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.FilterViewActivity;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.RedmineIssueListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.FilterArgument;
import jp.redmine.redmineclient.param.ProjectArgument;
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
	private static final String TAG = IssueList.class.getSimpleName();
	private static final int ACTIVITY_FILTER = 2001;
	private RedmineIssueListAdapter adapter;
	private SelectDataTask task;
	private MenuItem menu_refresh;
	private List<MenuItem> menu_project_spec = new ArrayList<MenuItem>();
	private View mFooter;
	private long lastPos = -1;

	private IssueView.OnArticleSelectedListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof ActivityInterface){
			mListener = ((ActivityInterface)activity).getHandler( IssueView.OnArticleSelectedListener.class);
		}
		if(mListener == null) {
			//setup empty events
			mListener = new  IssueView.OnArticleSelectedListener() {
				@Override
				public void onIssueFilterList(int connectionId, int filterid) {}
				@Override
				public void onIssueList(int connectionId, long projectId) {}
				@Override
				public void onIssueSelected(int connectionid, int issueid) {}
				@Override
				public void onIssueEdit(int connectionid, int issueid) {}
				@Override
				public void onIssueRefreshed(int connectionid, int issueid) {}
				@Override
				public void onIssueAdd(int connectionId, long projectId) {}
			};
		}

	}
	public IssueList(){
		super();
	}

	static public IssueList newInstance(ProjectArgument arg){
		IssueList fragment = new IssueList();
		fragment.setArguments(arg.getArgument());
		return fragment;
	}

	@Override
	public void onDestroyView() {
		cancelTask();
		setListAdapter(null);
		super.onDestroyView();
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

		getListView().setFastScrollEnabled(true);

		adapter = new RedmineIssueListAdapter(getHelper());
		FilterArgument intent = new FilterArgument();
		intent.setArgument( getArguments() );
		if(intent.hasFilterId()){
			adapter.setupParameter(intent.getConnectionId(),intent.getFilterId());
			for(MenuItem item : menu_project_spec)
				item.setVisible(false);
		} else {
			adapter.setupParameter(intent.getConnectionId(),intent.getProjectId());
		}
		adapter.notifyDataSetInvalidated();
		adapter.notifyDataSetChanged();
		if(adapter.getCount() < 1){
			this.onRefresh(true);
		}
		setListAdapter(adapter);

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
				mListener.onIssueEdit(item.getConnectionId(), item.getIssueId());
				return true;
			}
		});

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
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	public void onListItemClick(ListView parent, View v, int position, long id) {
		super.onListItemClick(parent, v, position, id);
		ListView listView = (ListView) parent;
		Object listitem = listView.getItemAtPosition(position);
		if(listitem == null || ! RedmineIssue.class.isInstance(listitem)  )
		{
			return;
		}
		RedmineIssue item = (RedmineIssue) listitem;
		mListener.onIssueSelected(item.getConnectionId(), item.getIssueId());
	}

	protected void onRefresh(boolean isFlush){
		if(task != null && task.getStatus() == Status.RUNNING){
			return;
		}
		onRefreshList();
		if(lastPos != getListView().getChildCount()){
			lastPos = -1; //reset
		}

		FilterArgument intent = new FilterArgument();
		intent.setArgument(getArguments());
		DatabaseCacheHelper helper = getHelper();
		ConnectionModel mConnection = new ConnectionModel(getActivity());
		RedmineConnection connection = mConnection.getItem(intent.getConnectionId());
		mConnection.finalize();

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if(intent.hasFilterId())
			task = new SelectDataTask(helper,connection,intent.getFilterId());
		else
			task = new SelectDataTask(helper,connection,intent.getProjectId());

		task.setFetchAll(sp.getBoolean("issue_get_all", false));
		task.execute(0,10,isFlush ? 1 : 0);
		if(isFlush && !intent.hasFilterId()){
			RedmineProject project = null;
			RedmineProjectModel mProject = new RedmineProjectModel(helper);
			try {
				project = mProject.fetchById(intent.getProjectId());
			} catch (SQLException e) {
				Log.e(TAG,"SelectDataTask",e);
			}
			SelectProjectEnumerationTask enumtask = new SelectProjectEnumerationTask(helper,connection,project);
			enumtask.execute();
		}
	}
	protected void onRefreshList(){
		if(adapter == null)
			return;
		adapter.notifyDataSetInvalidated();
		adapter.notifyDataSetChanged();

	}

	private class SelectDataTask extends SelectIssueTask {
		public SelectDataTask(DatabaseCacheHelper helper,RedmineConnection connection, long project) {
			super(helper,connection,project);
		}
		public SelectDataTask(DatabaseCacheHelper helper,RedmineConnection connection, int filter) {
			super(helper,connection,filter);
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
			onRefreshList();
			if(menu_refresh != null)
				menu_refresh.setEnabled(true);
		}

		@Override
		protected void onProgress(int max, int proc) {
			onRefreshList();
			super.onProgress(max, proc);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate( R.menu.issues, menu );
		menu_refresh = menu.findItem(R.id.menu_refresh);
		menu_project_spec.add(menu.findItem(R.id.menu_access_addnew));
		menu_project_spec.add(menu.findItem(R.id.menu_issues_filter));
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
				ProjectArgument intent = new ProjectArgument();
				intent.setArgument( getArguments() );
				ProjectArgument send = new ProjectArgument();
				send.setIntent( getActivity(), FilterViewActivity.class );
				send.setConnectionId(intent.getConnectionId());
				send.setProjectId(intent.getProjectId());
				startActivityForResult(send.getIntent(), ACTIVITY_FILTER);
				return true;
			}
			case R.id.menu_access_addnew:
			{
				ProjectArgument intent = new ProjectArgument();
				intent.setArgument( getArguments() );
				mListener.onIssueAdd(intent.getConnectionId(), intent.getProjectId());
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
