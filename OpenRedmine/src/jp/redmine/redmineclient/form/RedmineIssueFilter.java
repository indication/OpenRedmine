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
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class RedmineIssueFilter {
	private HashMap<String,RedmineIssueFilterExpander> dic = new HashMap<String,RedmineIssueFilterExpander>();
	public Button buttonSave;
	public TabHost tabHost;

	protected void addTab(Activity context,int label,int container){
		TabSpec spec1=tabHost.newTabSpec(context.getString(label));
		spec1.setIndicator(context.getString(label));
		spec1.setContent(container);
		tabHost.addTab(spec1);
	}

	public void setup(Activity activity, DatabaseCacheHelper helper, int connection, long project){
		buttonSave = (Button)activity.findViewById(R.id.buttonSave);
		tabHost=(TabHost)activity.findViewById(android.R.id.tabhost);
		tabHost.setup();

		RedmineIssueFilterExpander expStatus = generate(activity, R.id.listViewStatus);
		addList(expStatus,activity,connection,project, new RedmineStatusModel(helper));
		addTab(activity,R.string.ticket_status,R.id.tab1);

		RedmineIssueFilterExpander expVersion = generate(activity,R.id.listViewVersion);
		addList(expVersion,activity,connection,project, new RedmineVersionModel(helper));
		addTab(activity,R.string.ticket_version,R.id.tab2);

		RedmineIssueFilterExpander expCategory = generate(activity, R.id.listViewCategory);
		addList(expCategory,activity,connection,project, new RedmineCategoryModel(helper));
		addTab(activity,R.string.ticket_category,R.id.tab3);

		RedmineIssueFilterExpander expTracker = generate(activity, R.id.listViewTracker);
		addList(expTracker,activity,connection,project, new RedmineTrackerModel(helper));
		addTab(activity,R.string.ticket_tracker,R.id.tab4);
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
	public RedmineIssueFilterExpander generate(Activity activity,int listid){
		RedmineIssueFilterExpander ex = new RedmineIssueFilterExpander();
		ex.setup(activity, listid);
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
			filter = mFilter.fetchByCurrent(connection, project);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		setFilter(filter);

	}

}