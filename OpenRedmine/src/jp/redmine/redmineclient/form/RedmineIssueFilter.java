package jp.redmine.redmineclient.form;

import java.sql.SQLException;
import java.util.HashMap;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.RedmineFilterListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.IMasterModel;
import jp.redmine.redmineclient.db.cache.RedmineCategoryModel;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.DummySelection;
import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;

import android.app.Activity;
import android.widget.ListAdapter;

public class RedmineIssueFilter {
	private HashMap<String,RedmineIssueFilterExpander> dic = new HashMap<String,RedmineIssueFilterExpander>();


	public void setup(Activity activity, DatabaseCacheHelper helper, int connection, long project){
		RedmineIssueFilterExpander expStatus = generate(activity, R.id.checkBoxStatus,R.id.viewStatus,R.id.listViewStatus);
		addList(expStatus,activity,connection,project, new RedmineStatusModel(helper));

		RedmineIssueFilterExpander expVersion = generate(activity, R.id.checkBoxVersion,R.id.viewVersion,R.id.listViewVersion);
		addList(expVersion,activity,connection,project, new RedmineVersionModel(helper));

		RedmineIssueFilterExpander expCategory = generate(activity, R.id.checkBoxCategory,R.id.viewCategory,R.id.listViewCategory);
		addList(expCategory,activity,connection,project, new RedmineCategoryModel(helper));

		RedmineIssueFilterExpander expTracker = generate(activity, R.id.checkBoxTracker,R.id.viewTracker,R.id.listViewTracker);
		addList(expTracker,activity,connection,project, new RedmineTrackerModel(helper));
	}
	public void setupEvents(){
		for(RedmineIssueFilterExpander ex: dic.values()){
			ex.setupEvent();
		}
	}

	public void addList(RedmineIssueFilterExpander ex,Activity activity, int connection, long project, IMasterModel<? extends IMasterRecord> master ){
		RedmineFilterListAdapter adapter = new RedmineFilterListAdapter(master,connection,project);
		adapter.setupDummyItem(activity.getApplicationContext());
		addList(ex, adapter, master.getClass().getSimpleName());
	}
	public void addList(RedmineIssueFilterExpander ex, ListAdapter adapter, String key ){
		ex.adapter = adapter;
		dic.put(key, ex);
	}
	public RedmineIssueFilterExpander generate(Activity activity,int expanderid,int checkid,int listid){
		RedmineIssueFilterExpander ex = new RedmineIssueFilterExpander();
		ex.setup(activity, expanderid, checkid, listid);
		return ex;
	}

	public void setFilter(RedmineFilter filter){
		if(filter == null)
			filter = new RedmineFilter();
		setFilter(RedmineStatusModel.class,filter.getStatus());
		setFilter(RedmineVersionModel.class,filter.getVersion());
		setFilter(RedmineCategoryModel.class,filter.getCategory());
		setFilter(RedmineTrackerModel.class,filter.getTracker());

	}

	protected void setFilter(Class<?> key,IMasterRecord rec){
		RedmineIssueFilterExpander ex = dic.get(key.getSimpleName());
		if(ex!=null){
			ex.selectItem(rec);
		}
	}
	public RedmineFilter getFilter(RedmineFilter filter){
		if(filter == null)
			filter = new RedmineFilter();
		filter.setStatus((RedmineStatus)getFilter(RedmineStatusModel.class));
		filter.setVersion((RedmineProjectVersion)getFilter(RedmineVersionModel.class));
		filter.setCategory((RedmineProjectCategory)getFilter(RedmineCategoryModel.class));
		filter.setTracker((RedmineTracker)getFilter(RedmineTrackerModel.class));
		return filter;
	}
	protected IMasterRecord getFilterRaw(Class<?> key){
		RedmineIssueFilterExpander ex = dic.get(key.getSimpleName());
		if(ex==null)
			return null;
		return ex.getSelectedItem();
	}

	protected IMasterRecord getFilter(Class<?> key){
		IMasterRecord rec = getFilterRaw(key);
		if(rec == null || rec instanceof DummySelection)
			return null;
		return rec;
	}

	public void setFilter(DatabaseCacheHelper helper, int connection, long project){

		RedmineFilterModel mFilter = new RedmineFilterModel(helper);
		RedmineFilter filter = null;
		try {
			filter = mFilter.fetchByCurrnt(connection, project);
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		setFilter(filter);

	}

}
