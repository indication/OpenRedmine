package jp.redmine.redmineclient.fragment;

import android.annotation.TargetApi;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.ListFragmentSwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.WebViewActivity;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.adapter.NewsListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.fragment.helper.SwipeRefreshLayoutHelper;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.ProjectArgument;
import jp.redmine.redmineclient.param.WebArgument;
import jp.redmine.redmineclient.task.SelectNewsTask;

public class NewsList extends OrmLiteListFragment<DatabaseCacheHelper> implements SwipeRefreshLayout.OnRefreshListener {
	private static final String TAG = NewsList.class.getSimpleName();
	private NewsListAdapter adapter;
	private SelectNewsTask task;
	private MenuItem menu_refresh;
	private View mFooter;
	private WebviewActionInterface mListener;

	public NewsList(){
		super();
	}

	static public NewsList newInstance(ProjectArgument intent){
		NewsList instance = new NewsList();
		instance.setArguments(intent.getArgument());
		return instance;
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

		mListener = ActivityHandler.getHandler(getActivity(), WebviewActionInterface.class);
		getListView().addFooterView(mFooter);
		getListView().setFastScrollEnabled(true);

		getListView().setTextFilterEnabled(true);


		adapter = new NewsListAdapter(getHelper(), getActivity(), mListener);

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
		View view = super.onCreateView(inflater, container, savedInstanceState);
		ListFragmentSwipeRefreshLayout.ViewRefreshLayout result
				= ListFragmentSwipeRefreshLayout.inject(container, view);
		mSwipeRefreshLayout = result.layout;
		SwipeRefreshLayoutHelper.setEvent(mSwipeRefreshLayout, this);
		return result.parent;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}

	SwipeRefreshLayout mSwipeRefreshLayout;
	@Override
	public void onListItemClick(ListView listView, View v, int position, long id) {
		super.onListItemClick(listView, v, position, id);
		Object item =  listView.getItemAtPosition(position);
		if(!(item instanceof RedmineProject))
			return;
		RedmineProject project = (RedmineProject)item;
		//mListener.onIssueList(project.getConnectionId(), project.getId());
	}

	public void onRefresh(){
		if(task != null && task.getStatus() == Status.RUNNING){
			return;
		}
		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());
		int id = intent.getConnectionId();
		RedmineConnection connection = ConnectionModel.getItem(getActivity(), id);
		RedmineProjectModel mProject = new RedmineProjectModel(getHelper());
		task = new SelectNewsTask(getHelper(),connection);
		task.setupEventWithRefresh(mFooter, menu_refresh, mSwipeRefreshLayout, (data) -> adapter.notifyDataSetChanged());
		task.setOnProgressHandler((max, proc) -> adapter.notifyDataSetChanged());
		try {
			RedmineProject proj = mProject.fetchById(intent.getProjectId());
			task.execute(proj);
		} catch (SQLException e) {
			Log.e(TAG, "onRefresh", e);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate( R.menu.refresh, menu );
		menu_refresh = menu.findItem(R.id.menu_refresh);
		if(task != null && task.getStatus() == Status.RUNNING)
			menu_refresh.setEnabled(false);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			setupSearchBar(menu);
		inflater.inflate( R.menu.web, menu );
		super.onCreateOptionsMenu(menu, inflater);
	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected void setupSearchBar(Menu menu){
		SearchView search = new SearchView(getActivity());
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
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
		;
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
			case R.id.menu_web:
			{
				ProjectArgument input = new ProjectArgument();
				input.setArgument(getArguments());
				RedmineProject project = null;
				RedmineProjectModel mProject = new RedmineProjectModel(getHelper());
				try {
					project = mProject.fetchById(input.getProjectId());
				} catch (SQLException e) {
					Log.e(TAG,"onOptionsItemSelected",e);
					return false;
				}
				WebArgument intent = new WebArgument();
				intent.setIntent(getActivity().getApplicationContext(), WebViewActivity.class);
				intent.importArgument(input);

				intent.setUrl("/projects/"
						+ ((project == null || project.getName() == null) ? "" : project.getName())
						+ "/news"
				);
				getActivity().startActivity(intent.getIntent());
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

}
