package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.IssueActionEmptyHandler;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.NewsListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.param.ProjectArgument;
import jp.redmine.redmineclient.task.SelectNewsTask;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class NewsList extends OrmLiteListFragment<DatabaseCacheHelper> {
	private static final String TAG = NewsList.class.getSimpleName();
	private NewsListAdapter adapter;
	private SelectDataTask task;
	private MenuItem menu_refresh;
	private View mFooter;
	private IssueActionInterface mListener;

	public NewsList(){
		super();
	}

	static public NewsList newInstance(ProjectArgument intent){
		NewsList instance = new NewsList();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof ActivityInterface){
			ActivityInterface aif = (ActivityInterface)activity;
			mListener = aif.getHandler(IssueActionInterface.class);
		}
		if(mListener == null) {
			//setup empty events
			mListener = new IssueActionEmptyHandler();
		}

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

		getListView().setTextFilterEnabled(true);


		adapter = new NewsListAdapter(getHelper(), getActivity());

		final ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());

		setListAdapter(adapter);
		adapter.setupParameter(intent.getConnectionId(), intent.getProjectId());
		adapter.notifyDataSetChanged();

		if(adapter.getCount() < 1){
			onRefresh();
		}

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
	public void onResume() {
		super.onResume();
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}

	private PullToRefreshLayout mPullToRefreshLayout;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// This is the View which is created by ListFragment
		ViewGroup viewGroup = (ViewGroup) view;

		// We need to create a PullToRefreshLayout manually
		mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

		// We can now setup the PullToRefreshLayout
		ActionBarPullToRefresh.from(getActivity())
				// We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
				.insertLayoutInto(viewGroup)

						// We need to mark the ListView and it's Empty View as pullable
						// This is because they are not dirent children of the ViewGroup
				.theseChildrenArePullable(android.R.id.list, android.R.id.empty)

						// We can now complete the setup as desired
				.listener(new OnRefreshListener() {
					@Override
					public void onRefreshStarted(View view) {
						onRefresh();
					}
				})
				.setup(mPullToRefreshLayout);
	}
	@Override
	public void onListItemClick(ListView listView, View v, int position, long id) {
		super.onListItemClick(listView, v, position, id);
		Object item =  listView.getItemAtPosition(position);
		if(item == null || !(item instanceof RedmineProject))
			return;
		RedmineProject project = (RedmineProject)item;
		//mListener.onIssueList(project.getConnectionId(), project.getId());
	}

	protected void onRefresh(){
		if(task != null && task.getStatus() == Status.RUNNING){
			return;
		}
		ConnectionArgument intent = new ConnectionArgument();
		intent.setArgument(getArguments());
		int id = intent.getConnectionId();
		ConnectionModel mConnection = new ConnectionModel(getActivity());
		RedmineConnection connection = mConnection.getItem(id);
			mConnection.finalize();
		task = new SelectDataTask(getHelper(),connection);
		task.execute();
	}

	private class SelectDataTask extends SelectNewsTask {
		public SelectDataTask(DatabaseCacheHelper helper, RedmineConnection con) {
			super(helper, con);
		}

		// can use UI thread here
		@Override
		protected void onPreExecute() {
			mFooter.setVisibility(View.VISIBLE);
			if(menu_refresh != null)
				menu_refresh.setEnabled(false);
			if(mPullToRefreshLayout != null && !mPullToRefreshLayout.isRefreshing())
				mPullToRefreshLayout.setRefreshing(true);
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(Void b) {
			mFooter.setVisibility(View.GONE);
			adapter.notifyDataSetInvalidated();
			adapter.notifyDataSetChanged();
			if(menu_refresh != null)
				menu_refresh.setEnabled(true);
			if(mPullToRefreshLayout != null)
				mPullToRefreshLayout.setRefreshComplete();
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

		if(getActivity() instanceof SherlockFragmentActivity){
			ActionBar bar = ((SherlockFragmentActivity)getActivity()).getSupportActionBar();
			SearchView search = new SearchView(bar.getThemedContext());
			search.setIconifiedByDefault(false);
			search.setSubmitButtonEnabled(true);
			search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String s) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String s) {
					if (TextUtils.isEmpty(s)) {
						getListView().clearTextFilter();
					} else {
						getListView().setFilterText(s);
					}
					return true;
				}
			});
			menu.add(android.R.string.search_go)
					.setIcon(android.R.drawable.ic_menu_search)
					.setActionView(search)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}
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
		}
		return super.onOptionsItemSelected(item);
	}

}
