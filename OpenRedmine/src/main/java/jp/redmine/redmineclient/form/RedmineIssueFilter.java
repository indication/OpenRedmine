package jp.redmine.redmineclient.form;

import java.sql.SQLException;
import java.util.HashMap;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.FilterListAdapter;
import jp.redmine.redmineclient.adapter.FilterSortListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.IMasterModel;
import jp.redmine.redmineclient.db.cache.RedmineCategoryModel;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedminePriorityModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.DummySelection;
import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineStatus;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.SwitchCompat;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class RedmineIssueFilter {
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer,RedmineIssueFilterExpander> dic = new HashMap<Integer,RedmineIssueFilterExpander>();
	public Button buttonSave;
	public TabHost tabHost;
	public SwitchCompat toggleClosed;
	public RadioButton radioClosedOn;
	public RadioButton radioClosedOff;
	public RadioGroup radioClosed;
	protected ArrayAdapter<RedmineFilterSortItem> adapterSort;
	RedmineFilterModel mFilter;

	protected void addTab(Activity context,int label,int container, Integer icon){
		TabSpec spec1=tabHost.newTabSpec(context.getString(label));
		if(icon == null)
			spec1.setIndicator(context.getString(label));
		else
			spec1.setIndicator(context.getString(label),context.getResources().getDrawable(icon));
		spec1.setContent(container);
		tabHost.addTab(spec1);
	}

	public void setup(final Activity activity, DatabaseCacheHelper helper){
		if (tabHost != null)
			return;
		toggleClosed = activity.findViewById(R.id.toggleClosed);
		radioClosed = activity.findViewById(R.id.radioClosed);
		radioClosedOn =  activity.findViewById(R.id.radioClosedOn);
		radioClosedOff =  activity.findViewById(R.id.radioClosedOff);
		radioClosedOff.setChecked(true);
		buttonSave = activity.findViewById(R.id.buttonSave);
		tabHost=activity.findViewById(android.R.id.tabhost);
		tabHost.setup();
		mFilter = new RedmineFilterModel(helper);

		addTab(activity,R.string.ticket_other,R.id.tab9,null);

		RedmineIssueFilterExpander expStatus = generate(activity, R.id.listViewStatus);
		addList(expStatus,activity, new RedmineStatusModel(helper),R.string.ticket_status);
		addTab(activity,R.string.ticket_status,R.id.tab1,null);

		RedmineIssueFilterExpander expVersion = generate(activity,R.id.listViewVersion);
		addList(expVersion,activity, new RedmineVersionModel(helper),R.string.ticket_version);
		addTab(activity,R.string.ticket_version,R.id.tab2,null);

		RedmineIssueFilterExpander expCategory = generate(activity, R.id.listViewCategory);
		addList(expCategory,activity, new RedmineCategoryModel(helper),R.string.ticket_category);
		addTab(activity,R.string.ticket_category,R.id.tab3,null);

		RedmineIssueFilterExpander expTracker = generate(activity, R.id.listViewTracker);
		addList(expTracker,activity, new RedmineTrackerModel(helper),R.string.ticket_tracker);
		addTab(activity,R.string.ticket_tracker,R.id.tab4,null);

		RedmineIssueFilterExpander expPriority = generate(activity, R.id.listViewPriority);
		addList(expPriority,activity, new RedminePriorityModel(helper),R.string.ticket_priority);
		addTab(activity,R.string.ticket_priority,R.id.tab5,null);

		RedmineIssueFilterExpander expUserCreated = generate(activity, R.id.listViewUserCreated);
		addList(expUserCreated,activity, new RedmineUserModel(helper),R.string.ticket_author);
		addTab(activity,R.string.ticket_author,R.id.tab6,null);

		RedmineIssueFilterExpander expUserAssing = generate(activity, R.id.listViewUserAssing);
		addList(expUserAssing,activity, new RedmineUserModel(helper),R.string.ticket_assigned);
		addTab(activity,R.string.ticket_assigned,R.id.tab7,null);

		RedmineIssueFilterExpander expSort = generate(activity, R.id.listViewSort);
		addListSort(expSort,activity, R.string.ticket_sort);
		addTab(activity,R.string.ticket_sort,R.id.tab8,null);
	}
	public void setupParameter(int connection, long project){
		for(RedmineIssueFilterExpander ex: dic.values()){
			if(ex.adapter instanceof FilterListAdapter)
				((FilterListAdapter)ex.adapter).setupParameter(connection, project);
		}
	}

	public void setupEvents(){
		for(RedmineIssueFilterExpander ex: dic.values()){
			ex.setupEvent();
		}
		CompoundButton.OnCheckedChangeListener handler = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				radioClosed.setEnabled(isChecked);
				radioClosedOn.setEnabled(isChecked);
				radioClosedOff.setEnabled(isChecked);
			}
		};
		toggleClosed.setOnCheckedChangeListener(handler);
		handler.onCheckedChanged(toggleClosed, toggleClosed.isChecked());
	}
	public void refresh(){
		for(RedmineIssueFilterExpander ex: dic.values()){
			ex.refresh();
		}
	}
	public void addListSort(RedmineIssueFilterExpander ex,final Activity activity, int key ){
		ex.adapter = new FilterSortListAdapter();
		dic.put(key, ex);
	}

	public void addList(RedmineIssueFilterExpander ex,Activity activity, IMasterModel<? extends IMasterRecord> master, int key ){
		FilterListAdapter adapter = new FilterListAdapter(master);
		adapter.setupDummyItem(activity.getApplicationContext());
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
		setFilter(R.string.ticket_status,filter.getStatus());
		setFilter(R.string.ticket_version,filter.getVersion());
		setFilter(R.string.ticket_category,filter.getCategory());
		setFilter(R.string.ticket_tracker,filter.getTracker());
		setFilter(R.string.ticket_priority,filter.getPriority());
		setFilter(R.string.ticket_author,filter.getAuthor());
		setFilter(R.string.ticket_assigned,filter.getAssigned());
		setFilter(R.string.ticket_sort,RedmineFilterSortItem.setupFilter(new RedmineFilterSortItem(), filter.getSort()));
		toggleClosed.setChecked(filter.isClosed() != null);
		if(filter.isClosed() != null)
			(filter.isClosed() ? radioClosedOn : radioClosedOff).setChecked(true);

	}

	protected void setFilter(int key,IMasterRecord rec){
		RedmineIssueFilterExpander ex = dic.get(key);
		if(ex!=null){
			ex.selectItem(rec);
		}
	}
	public RedmineFilter getFilter(RedmineFilter filter){
		if(filter == null)
			filter = new RedmineFilter();
		filter.setStatus((RedmineStatus)			getFilter(R.string.ticket_status));
		filter.setVersion((RedmineProjectVersion)	getFilter(R.string.ticket_version));
		filter.setCategory((RedmineProjectCategory)	getFilter(R.string.ticket_category));
		filter.setTracker((RedmineTracker)			getFilter(R.string.ticket_tracker));
		filter.setPriority((RedminePriority)		getFilter(R.string.ticket_priority));
		filter.setAuthor((RedmineUser)				getFilter(R.string.ticket_author));
		filter.setAssigned((RedmineUser)			getFilter(R.string.ticket_assigned));

		filter.setSort(RedmineFilterSortItem.getFilter((RedmineFilterSortItem)getFilter(R.string.ticket_sort)));
		filter.setClosed(toggleClosed.isChecked() ? radioClosedOn.isChecked() : null);
		return filter;
	}
	protected IMasterRecord getFilterRaw(int key){
		RedmineIssueFilterExpander ex = dic.get(key);
		if(ex==null)
			return null;
		return ex.getSelectedItem();
	}

	protected IMasterRecord getFilter(int key){
		IMasterRecord rec = getFilterRaw(key);
		if(rec == null || rec instanceof DummySelection)
			return null;
		return rec;
	}

	public void setFilter(int connection, long project){
		RedmineFilter filter = null;
		try {
			filter = mFilter.fetchByCurrent(connection, project);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		setFilter(filter);

	}

}
