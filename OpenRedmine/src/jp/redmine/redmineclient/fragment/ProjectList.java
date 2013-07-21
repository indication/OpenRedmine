package jp.redmine.redmineclient.fragment;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.ConnectionActivity;
import jp.redmine.redmineclient.IssueListActivity;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.RedmineProjectListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.intent.ConnectionIntent;
import jp.redmine.redmineclient.intent.ProjectIntent;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.task.SelectProjectTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ProjectList extends OrmLiteListFragment<DatabaseCacheHelper> {
	private RedmineProjectListAdapter adapter;
	private SelectDataTask task;
	private MenuItem menu_refresh;
	private View mFooter;

	public ProjectList(){
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

		adapter = new RedmineProjectListAdapter(getHelper());
		setListAdapter(adapter);

		getListView().setFastScrollEnabled(true);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFooter = inflater.inflate(R.layout.listview_footer, container, false);
		mFooter.setVisibility(View.GONE);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		if(adapter != null){
			ConnectionIntent intent = new ConnectionIntent(getActivity().getIntent());
			adapter.setupParameter(intent.getConnectionId());
			adapter.notifyDataSetInvalidated();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		super.onListItemClick(parent, v, position, id);
		ListView listView = (ListView) parent;
		Object item =  listView.getItemAtPosition(position);
		if(item == null || !(item instanceof RedmineProject))
			return;
		onItemSelect((RedmineProject)item);
	}

	protected void onItemSelect(RedmineProject item) {
		ProjectIntent intent = new ProjectIntent( getActivity().getApplicationContext(), IssueListActivity.class );
		intent.setConnectionId(item.getConnectionId());
		intent.setProjectId(item.getId());
		startActivity( intent.getIntent() );
	}

	protected void onRefresh(){
		if(task != null && task.getStatus() == Status.RUNNING){
			return;
		}
			ConnectionIntent intent = new ConnectionIntent(getActivity().getIntent());
			int id = intent.getConnectionId();
		ConnectionModel mConnection = new ConnectionModel(getActivity());
		RedmineConnection connection = mConnection.getItem(id);
			mConnection.finalize();
		task = new SelectDataTask(getHelper());
		task.execute(connection);
		}

	private class SelectDataTask extends SelectProjectTask {
		public SelectDataTask(DatabaseCacheHelper helper) {
			super(helper);
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
		inflater.inflate( R.menu.projects, menu );
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
				this.onRefresh();
				return true;
			}
			case R.id.menu_settings:
			{
				ConnectionIntent input = new ConnectionIntent(getActivity().getIntent());
				ConnectionIntent intent = new ConnectionIntent( getActivity().getApplicationContext(), ConnectionActivity.class );
				intent.setConnectionId(input.getConnectionId());
				startActivity( intent.getIntent() );
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

}
