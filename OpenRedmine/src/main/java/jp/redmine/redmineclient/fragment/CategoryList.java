package jp.redmine.redmineclient.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.CategoryListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.ProjectArgument;

public class CategoryList extends OrmLiteListFragment<DatabaseCacheHelper> {
	private static final String TAG = CategoryList.class.getSimpleName();
	private CategoryListAdapter adapter;

	private IssueActionInterface mListener;

	public CategoryList(){
		super();
	}

	static public CategoryList newInstance(ProjectArgument arg){
		CategoryList fragment = new CategoryList();
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

		adapter = new CategoryListAdapter(getHelper(), getActivity());
		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());
		adapter.setupParameter(intent.getConnectionId(),intent.getProjectId());
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
		if(listitem == null || !RedmineProjectCategory.class.isInstance(listitem)  )
		{
			return;
		}
		RedmineProjectCategory item = (RedmineProjectCategory) listitem;

		RedmineFilter filter = new RedmineFilter();
		filter.setName(getString(R.string.title_category,item.getName()));
		filter.setConnectionId(item.getConnectionId());
		filter.setProject(item.getProject());
		filter.setCategory(item);
		filter.setSort(RedmineFilterSortItem.getFilter(RedmineFilterSortItem.KEY_MODIFIED, false));
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
