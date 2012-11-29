package jp.redmine.redmineclient.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.IMasterModel;
import jp.redmine.redmineclient.db.cache.RedmineCategoryModel;
import jp.redmine.redmineclient.db.cache.RedminePriorityModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineTrackerModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.IMasterRecord;

import android.content.Context;

public class FilterListAdapterModel {

	private RedmineUserModel mUser;
	private RedmineTrackerModel mTracker;
	private RedminePriorityModel mPriority;
	private RedmineCategoryModel mCategory;
	private List<AdapterGroup> groups = new ArrayList<AdapterGroup>();
	private long project_id;
	private int connection_id;

	class AdapterGroup{
		public AdapterGroup(int i,IMasterModel<? extends IMasterRecord> m){
			id = i;
			model = m;
		}
		public String name;
		public int id;
		public IMasterModel<? extends IMasterRecord> model;
	}
	public void setupID(int connection, long project){
		connection_id = connection;
		project_id = project;
	}
	public FilterListAdapterModel(DatabaseCacheHelper helperCache){
		mUser = new RedmineUserModel(helperCache);
		mTracker = new RedmineTrackerModel(helperCache);
		mCategory = new RedmineCategoryModel(helperCache);
		mPriority = new RedminePriorityModel(helperCache);
		groups.add(new AdapterGroup(R.string.ticket_status, new RedmineStatusModel(helperCache)));
		groups.add(new AdapterGroup(R.string.ticket_version, new RedmineVersionModel(helperCache)));
	}

	public void setupText(Context con){
		for(AdapterGroup item: groups){
			item.name = con.getString(item.id);
		}
	}

	public String getGroup(int position){
		AdapterGroup grp = groups.get(position);
		return (grp!=null) ? grp.name : null;
	}

	public int getChildCount(int position) throws SQLException{
		AdapterGroup grp = groups.get(position);
		return (int) grp.model.countByProject(connection_id, project_id);
	}

	public IMasterRecord getChild(int group,int child) throws SQLException{
		AdapterGroup grp = groups.get(group);
		return grp.model.fetchItemByProject(connection_id, project_id, child, 1);
	}

	public int getGroupCount() {
		return groups.size();
	}

}
