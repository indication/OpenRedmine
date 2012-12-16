package jp.redmine.redmineclient.form;

import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.RedmineFilterListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.IMasterModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.IMasterRecord;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class RedmineIssueFilter {
	private List<Expander> lists = new ArrayList<Expander>();

	class Expander{
		public CheckBox checkbox;
		public CheckBox expander;
		public ListAdapter adapter;
		public ListView list;
		public void setup(Activity activity,int checkid,int expanderid,int listid){
			checkbox = (CheckBox)activity.findViewById(checkid);
			expander = (CheckBox)activity.findViewById(expanderid);
			list = (ListView)activity.findViewById(listid);

		}
		public void setupEvent(){
			expander.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					list.setVisibility(isChecked ? View.VISIBLE : View.GONE);
				}
			});
			checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(list.getVisibility() == View.VISIBLE){
						if(!isChecked){
							checkbox.setChecked(false);
							//list.setVisibility(View.GONE);
						}
					}
				}
			});
			if(adapter != null){
				list.setAdapter(adapter);
			}
		}

	}


	public void setup(Activity activity, DatabaseCacheHelper helper, int connection, long project){
		Expander expStatus = generate(activity, R.id.checkBoxStatus,R.id.viewStatus,R.id.listViewStatus);
		addList(expStatus,activity,connection,project, new RedmineStatusModel(helper));
		Expander expVersion = generate(activity, R.id.checkBoxVersion,R.id.viewVersion,R.id.listViewVersion);
		addList(expVersion,activity,connection,project, new RedmineVersionModel(helper));
		//Expander expCategory = generate(activity, R.id.checkBoxCategory,R.id.viewCategory,R.id.listViewCategory);
		//addList(expVersion,activity,connection,project, new RedmineCategoryModel(helper));

	}
	public void setupEvents(){
		for(Expander ex : lists){
			ex.setupEvent();
		}
	}

	public void addList(Expander ex,Activity activity, int connection, long project, IMasterModel<? extends IMasterRecord> master ){
		RedmineFilterListAdapter adapter = new RedmineFilterListAdapter(master,connection,project);
		adapter.setupDummyItem(activity.getApplicationContext());
		addList(ex, adapter);
	}
	public void addList(Expander ex, ListAdapter adapter ){
		ex.adapter = adapter;
		lists.add(ex);
	}
	public Expander generate(Activity activity,int expanderid,int checkid,int listid){
		Expander ex = new Expander();
		ex.setup(activity, expanderid, checkid, listid);
		return ex;
	}

}
