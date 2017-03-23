package jp.redmine.redmineclient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.RecentConnectionIssueListAdapter;
import jp.redmine.redmineclient.adapter.RecentIssueListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineRecentIssue;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.ConnectionArgument;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class RecentIssueList extends OrmLiteFragment<DatabaseCacheHelper> {
	private BaseAdapter adapter;
	private IssueActionInterface mListener;
	private StickyListHeadersListView list;

	public RecentIssueList(){
		super();
	}

	static public RecentIssueList newInstance(){
		RecentIssueList fragment = new RecentIssueList();
		return fragment;
	}

	static public RecentIssueList newInstance(ConnectionArgument intent){
		RecentIssueList fragment = new RecentIssueList();
		fragment.setArguments(intent.getArgument());
		return fragment;
	}

	@Override
	public void onDestroyView() {
		list.setAdapter(null);
		super.onDestroyView();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mListener = ActivityHandler.getHandler(getActivity(), IssueActionInterface.class);
		list.setFastScrollEnabled(true);

		ConnectionArgument intent = new ConnectionArgument();
		intent.setArgument( getArguments() );
		if (intent.getConnectionId() == -1){
			RecentIssueListAdapter _adapter = new RecentIssueListAdapter(getHelper(), getActivity());
			adapter = _adapter;
			list.setAdapter(_adapter);
		} else {
			RecentConnectionIssueListAdapter _adapter = new RecentConnectionIssueListAdapter(getHelper(), getActivity());
			_adapter.setParameter(intent.getConnectionId());
			adapter = _adapter;
			list.setAdapter(_adapter);
		}
		onRefreshList();

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Object listitem = parent.getItemAtPosition(position);
				if(listitem == null || ! RedmineRecentIssue.class.isInstance(listitem)  )
				{
					return;
				}
				RedmineRecentIssue item = (RedmineRecentIssue) listitem;
				mListener.onIssueSelected(item.getConnectionId(), item.getIssue().getIssueId());
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View current = inflater.inflate(R.layout.page_stickylistheaderslist, container, false);
		list = (StickyListHeadersListView)current.findViewById(R.id.list);
		return current;
	}
	@Override
	public void onResume() {
		super.onResume();
		onRefreshList();
	}


	protected void onRefreshList(){
		if(adapter == null)
			return;
		adapter.notifyDataSetChanged();
	}

}
