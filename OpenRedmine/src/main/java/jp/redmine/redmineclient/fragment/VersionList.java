package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.activity.handler.IssueActionEmptyHandler;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.RedmineVersionListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.param.ProjectArgument;

public class VersionList extends OrmLiteListFragment<DatabaseCacheHelper> {
	private static final String TAG = VersionList.class.getSimpleName();
	private RedmineVersionListAdapter adapter;

	private IssueActionInterface mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof ActivityInterface){
			mListener = ((ActivityInterface)activity).getHandler( IssueActionInterface.class);
		}
		if(mListener == null) {
			//setup empty events
			mListener = new  IssueActionEmptyHandler();
		}

	}
	public VersionList(){
		super();
	}

	static public VersionList newInstance(ProjectArgument arg){
		VersionList fragment = new VersionList();
		fragment.setArguments(arg.getArgument());
		return fragment;
	}

	@Override
	public void onDestroyView() {
		setListAdapter(null);
		super.onDestroyView();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().setFastScrollEnabled(true);

		adapter = new RedmineVersionListAdapter(getHelper());
		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());
		adapter.setupParameter(intent.getConnectionId(),intent.getProjectId());
		adapter.notifyDataSetChanged();
		setListAdapter(adapter);


	}

	/*
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
	*/


}
