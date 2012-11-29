package jp.redmine.redmineclient;


import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.adapter.RedmineFilterListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.intent.ProjectIntent;
import jp.redmine.redmineclient.model.FilterListAdapterModel;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.RadioButton;

public class FilterViewActivity extends OrmLiteBaseActivity<DatabaseCacheHelper>  {
	public FilterViewActivity(){
		super();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	private FilterListAdapterModel dataadapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_filter);


		ExpandableListView listView = (ExpandableListView)findViewById(R.id.expandableListView);
		dataadapter = new FilterListAdapterModel(getHelper());
		dataadapter.setupText(getApplicationContext());
		RedmineFilterListAdapter adapter = new RedmineFilterListAdapter(dataadapter);
		listView.setAdapter(adapter);

		listView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				RadioButton radio = (RadioButton)v;
				if(radio == null)
					return false;
				radio.toggle();
				return true;
			}
		});

	}

	@Override
	protected void onStart() {
		ProjectIntent intent = new ProjectIntent(getIntent());
		int connectionid = intent.getConnectionId();
		long projectid = intent.getProjectId();
		dataadapter.setupID(connectionid, projectid);
		super.onStart();
	}
}
