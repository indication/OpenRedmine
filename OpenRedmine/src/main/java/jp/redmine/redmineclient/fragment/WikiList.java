package jp.redmine.redmineclient.fragment;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.ListFragmentSwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.adapter.WikiListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineWiki;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.fragment.helper.SwipeRefreshLayoutHelper;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.ProjectArgument;
import jp.redmine.redmineclient.task.SelectWikiTask;

public class WikiList extends OrmLiteListFragment<DatabaseCacheHelper> implements SwipeRefreshLayout.OnRefreshListener {
	private static final String TAG = WikiList.class.getSimpleName();
	private WikiListAdapter adapter;
	private SelectWikiTask task;
	private View mFooter;
	private MenuItem menu_refresh;
	SwipeRefreshLayout mSwipeRefreshLayout;

	private WebviewActionInterface mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	protected void cancelTask(){
		// cleanup task
		if(task != null && task.getStatus() == AsyncTask.Status.RUNNING){
			task.cancel(true);
		}
	}
	public WikiList(){
		super();
	}

	static public WikiList newInstance(ProjectArgument arg){
		WikiList fragment = new WikiList();
		fragment.setArguments(arg.getArgument());
		return fragment;
	}

	@Override
	public void onDestroyView() {
		cancelTask();
		setListAdapter(null);
		super.onDestroyView();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mListener = ActivityHandler.getHandler(getActivity(), WebviewActionInterface.class);
		getListView().setFastScrollEnabled(true);
		getListView().setTextFilterEnabled(true);

		adapter = new WikiListAdapter(getHelper(), getActivity());
		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());
		adapter.setupParameter(intent.getConnectionId(),intent.getProjectId());
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();

        if(adapter.getCount() == 0)
            onRefresh();

	}

	@Override
	public void onResume() {
		super.onResume();
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView listView, View v, int position, long id) {
		super.onListItemClick(listView, v, position, id);
		Object listitem = listView.getItemAtPosition(position);
		if(listitem == null || !RedmineWiki.class.isInstance(listitem)  )
		{
			return;
		}
		RedmineWiki item = (RedmineWiki) listitem;
		mListener.wiki(item.getConnectionId(),item.getProject().getId(),item.getTitle());
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
	public void onRefresh(){
		if(task != null && task.getStatus() == AsyncTask.Status.RUNNING){
			return;
		}
		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());
		int id = intent.getConnectionId();
		RedmineConnection connection = ConnectionModel.getItem(getActivity(), id);
		task = new SelectWikiTask(getHelper(), connection, (long)intent.getProjectId());
		task.setupEventWithRefresh(mFooter, menu_refresh, mSwipeRefreshLayout, (data) -> adapter.notifyDataSetChanged());
		task.setOnProgressHandler((max, proc) -> adapter.notifyDataSetChanged());
		task.execute("");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh, menu);
		menu_refresh = menu.findItem(R.id.menu_refresh);
		if(task != null && task.getStatus() == AsyncTask.Status.RUNNING)
			menu_refresh.setEnabled(false);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			setupSearchBar(menu);
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
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected void setupSearchBar(Menu menu){

		SearchView search = new SearchView(getActivity());
		search.setIconifiedByDefault(false);
		search.setSubmitButtonEnabled(true);
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				return onQueryTextChange(s);
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
}
