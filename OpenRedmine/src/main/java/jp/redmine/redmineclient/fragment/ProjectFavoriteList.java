package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.IssueActionEmptyHandler;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.RedmineProjectFavoriteAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineProject;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProjectFavoriteList extends OrmLiteFragment<DatabaseCacheHelper> {
	private static final String TAG = ProjectFavoriteList.class.getSimpleName();
	private IssueActionInterface mListener;
	private StickyListHeadersListView list;

	public ProjectFavoriteList(){
		super();
	}

	static public ProjectFavoriteList newInstance(){
		ProjectFavoriteList instance = new ProjectFavoriteList();
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
		if(list  != null)
			list.setAdapter(null);
		super.onDestroyView();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		list.setFastScrollEnabled(true);

		RedmineProjectFavoriteAdapter adapter = new RedmineProjectFavoriteAdapter(getHelper(), getActivity().getApplicationContext());

		list.setAdapter(adapter);
		adapter.notifyDataSetChanged();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View current = inflater.inflate(R.layout.stickylistheaderslist, container, false);
		list = (StickyListHeadersListView)current.findViewById(R.id.list);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Object item =  adapterView.getItemAtPosition(position);
				if(item == null || !(item instanceof RedmineProject))
					return;
				RedmineProject project = (RedmineProject)item;
				mListener.onIssueList(project.getConnectionId(), project.getId());
			}
		});
		return current;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(list.getAdapter() != null && list.getAdapter() instanceof  BaseAdapter)
			((BaseAdapter)list.getAdapter()).notifyDataSetChanged();

	}
}
