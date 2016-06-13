package jp.redmine.redmineclient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.TimeentryActionInterface;
import jp.redmine.redmineclient.adapter.IssueTimeEntryListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.IssueArgument;

public class TimeEntryList extends OrmLiteListFragment<DatabaseCacheHelper> {
	private IssueTimeEntryListAdapter adapter;
	private View mFooter;
	private TimeentryActionInterface mListener;

	public TimeEntryList(){
		super();
	}

	static public TimeEntryList newInstance(IssueArgument intent){
		TimeEntryList instance = new TimeEntryList();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public void onDestroyView() {
		setListAdapter(null);
		super.onDestroyView();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mListener = ActivityHandler.getHandler(getActivity(), TimeentryActionInterface.class);
		getListView().addFooterView(mFooter);

		adapter = new IssueTimeEntryListAdapter(getHelper(),getActivity());
		setListAdapter(adapter);

		getListView().setFastScrollEnabled(true);
		onRefresh(true);
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
		Object item =  listView.getItemAtPosition(position);
		if(item == null || !(item instanceof RedmineTimeEntry))
			return;
		RedmineTimeEntry entry = (RedmineTimeEntry)item;
		mListener.onTimeEntrySelected(entry.getConnectionId(), entry.getIssueId(), entry.getTimeentryId());
	}

	protected void onRefresh(boolean isFetch){
		IssueArgument intent = new IssueArgument();
		intent.setArgument(getArguments());
		int connectionid = intent.getConnectionId();

		adapter.setupParameter(connectionid,intent.getIssueId());
		adapter.notifyDataSetInvalidated();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate( R.menu.timeentry_view, menu );
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
			case R.id.menu_access_addnew:
			{
				IssueArgument intent = new IssueArgument();
				intent.setArgument(getArguments());
				mListener.onTimeEntryAdd(intent.getConnectionId(), intent.getIssueId());
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
