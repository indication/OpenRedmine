package jp.redmine.redmineclient.fragment;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.TimeEntryEditActivity;
import jp.redmine.redmineclient.adapter.RedmineTimeEntryListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.param.TimeEntryArgument;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TimeEntryList extends OrmLiteListFragment<DatabaseCacheHelper> {
	private RedmineTimeEntryListAdapter adapter;
	private View mFooter;

	private final int FORM_TIMEENTRY = 1;

	public TimeEntryList(){
		super();
	}

	public static TimeEntryList newInstance() {
		return new TimeEntryList();
	}

	@Override
	public void onDestroyView() {
		setListAdapter(null);
		super.onDestroyView();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().addFooterView(mFooter);

		adapter = new RedmineTimeEntryListAdapter(getHelper());
		setListAdapter(adapter);

		getListView().setFastScrollEnabled(true);
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
	public void onStart() {
		super.onStart();
		onRefresh(true);
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		super.onListItemClick(parent, v, position, id);
		ListView listView = (ListView) parent;
		Object item =  listView.getItemAtPosition(position);
		if(item == null || !(item instanceof RedmineTimeEntry))
			return;
		onItemSelect((RedmineTimeEntry)item);
	}

	protected void onItemSelect(RedmineTimeEntry entry) {
		TimeEntryArgument send = new TimeEntryArgument();
		send.setIntent( getActivity().getApplicationContext(), TimeEntryEditActivity.class );
		send.setConnectionId(entry.getConnectionId());
		send.setIssueId(entry.getIssueId());
		send.setTimeEntryId(entry.getTimeentryId());
		startActivity(send.getIntent());
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
				TimeEntryArgument send = new TimeEntryArgument();
				send.setIntent( getActivity().getApplicationContext(), TimeEntryEditActivity.class );
				send.setConnectionId(intent.getConnectionId());
				send.setIssueId(intent.getIssueId());
				startActivityForResult(send.getIntent(),FORM_TIMEENTRY);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case FORM_TIMEENTRY:
			if(resultCode != Activity.RESULT_OK )
				break;
			getActivity().finish();
			break;
		default:
			break;
		}
	}
}
