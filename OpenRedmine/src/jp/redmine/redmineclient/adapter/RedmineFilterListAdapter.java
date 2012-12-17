package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.IMasterModel;
import jp.redmine.redmineclient.entity.DummySelection;
import jp.redmine.redmineclient.entity.IMasterRecord;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RedmineFilterListAdapter extends BaseAdapter {

	private IMasterModel<? extends IMasterRecord> model;
	protected int connection_id;
	protected long project_id;
	protected boolean addNone = false;
	private DummySelection dummyitem;


	protected DummySelection getDummyItem(){
		if(dummyitem == null)
			setupDummyItem(null);
		return dummyitem;
	}
	public void setupDummyItem(Context view){
		DummySelection record = new DummySelection();
		if(view == null)
			record.setName("none");
		else
			record.setName(view.getString(R.string.filter_none));
		record.setId(-1L);
		dummyitem = record;
	}

	public RedmineFilterListAdapter(IMasterModel<? extends IMasterRecord> m, int connection, long project){
		model = m;
		connection_id = connection;
		project_id = project;
		addNone = true;
	}

	@Override
	public int getCount() {
		int count = addNone ? 1 : 0;
		try {
			count += (int) model.countByProject(connection_id, project_id);
		} catch (SQLException e) {
			Log.e("RedmineFilterListItemAdapter::" + model.getClass().getName(),"getCount" , e);
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		int realpos = position - (addNone ? 1 : 0);
		switch(position){
		case 0:
			if(addNone){
				return getDummyItem();
			}
		default:
		}
		try {
			return model.fetchItemByProject(connection_id, project_id, realpos, 1);
		} catch (SQLException e) {
			Log.e("RedmineFilterListItemAdapter::" + model.getClass().getName(),"getItem" , e);
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		IMasterRecord rec = (IMasterRecord) getItem(position);
		if(rec == null){
			return -1;
		} else {
			return rec.getId();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if( convertView == null ){
			LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(android.R.layout.simple_list_item_single_choice, null);
		}
		setupData(position, convertView);
		return convertView;
	}

	protected void setupData(int position,View convertView){
		if( convertView == null )
			return;
		TextView text = (TextView)convertView.findViewById(android.R.id.text1);
		IMasterRecord rec = null;
		if(convertView.getTag() == null || !(convertView.getTag() instanceof IMasterRecord)){
			rec = (IMasterRecord)getItem(position);
			convertView.setTag(rec);
		} else {
			rec = (IMasterRecord)convertView.getTag();
		}
		if(rec == null){
			Log.e("RedmineFilterListItemAdapter::" + model.getClass().getName(),"setupData");
			return;
		}
		text.setText(rec.getName());
	}


}
