package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.activity.handler.IssueActionEmptyHandler;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.adapter.RedmineCategoryListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.param.ProjectArgument;

public class CategoryList extends OrmLiteListFragment<DatabaseCacheHelper> implements ActionBar.TabListener {
	private static final String TAG = CategoryList.class.getSimpleName();
	private RedmineCategoryListAdapter adapter;

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

		getListView().setFastScrollEnabled(true);

		adapter = new RedmineCategoryListAdapter(getHelper());
		ProjectArgument intent = new ProjectArgument();
		intent.setArgument(getArguments());
		adapter.setupParameter(intent.getConnectionId(),intent.getProjectId());
		adapter.notifyDataSetChanged();
		setListAdapter(adapter);


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

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		fragmentTransaction.replace(android.R.id.content, this);
		fragmentTransaction.attach(this);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		fragmentTransaction.detach(this);
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}
}
