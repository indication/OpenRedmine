package jp.redmine.redmineclient.form;

import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.RedmineFilterListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.IMasterModel;
import jp.redmine.redmineclient.db.cache.RedmineCategoryModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.IMasterRecord;

import android.app.Activity;
import android.widget.ListAdapter;

public class RedmineIssueFilter {
	private List<RedmineIssueFilterExpander> lists = new ArrayList<RedmineIssueFilterExpander>();


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
		for(RedmineIssueFilterExpander ex : lists){
			ex.setupEvent();
		}
	}

	public void addList(RedmineIssueFilterExpander ex,Activity activity, int connection, long project, IMasterModel<? extends IMasterRecord> master ){
		RedmineFilterListAdapter adapter = new RedmineFilterListAdapter(master,connection,project);
		adapter.setupDummyItem(activity.getApplicationContext());
		addList(ex, adapter);
	}
	public void addList(RedmineIssueFilterExpander ex, ListAdapter adapter ){
		ex.adapter = adapter;
		lists.add(ex);
	}
	public RedmineIssueFilterExpander generate(Activity activity,int expanderid,int checkid,int listid){
		RedmineIssueFilterExpander ex = new RedmineIssueFilterExpander();
		ex.setup(activity, expanderid, checkid, listid);
		return ex;
	}

}
