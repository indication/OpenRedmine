package jp.redmine.redmineclient.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.VersionListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.ProjectArgument;

public class VersionList extends OrmLiteListFragment<DatabaseCacheHelper> {
	private static final String TAG = VersionList.class.getSimpleName();
	private VersionListAdapter adapter;

	private IssueActionInterface mListener;

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

		mListener = ActivityHandler.getHandler(getActivity(), IssueActionInterface.class);
		getListView().setFastScrollEnabled(true);

		adapter = new VersionListAdapter(getHelper());
		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());
		adapter.setupParameter(intent.getConnectionId(), intent.getProjectId());
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();


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
		if(listitem == null || !RedmineProjectVersion.class.isInstance(listitem)  )
		{
			return;
		}
		RedmineProjectVersion item = (RedmineProjectVersion) listitem;

		RedmineFilter filter = new RedmineFilter();
		filter.setName(getString(R.string.title_version, item.getName()));
		filter.setConnectionId(item.getConnectionId());
		filter.setProject(item.getProject());
		filter.setVersion(item);
		filter.setSort(RedmineFilterSortItem.getFilter(RedmineFilterSortItem.KEY_DATE_DUE, false));
		RedmineFilterModel mFilter = new RedmineFilterModel(getHelper());
		try {
			RedmineFilter target = mFilter.getSynonym(filter);
			if(target == null){
				mFilter.insert(filter);
				target = filter;
			}
			mListener.onIssueFilterList(item.getConnectionId(), target.getId());
		} catch (SQLException e) {
			Log.e(TAG, "onListItemClick", e);
		}
	}

}
