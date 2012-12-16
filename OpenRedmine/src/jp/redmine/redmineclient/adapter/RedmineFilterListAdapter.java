package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.IMasterModel;
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

	public RedmineFilterListAdapter(IMasterModel<? extends IMasterRecord> m, int connection, long project){
		model = m;
		connection_id = connection;
		project_id = project;
	}

	@Override
	public int getCount() {
		try {
			return (int) model.countByProject(connection_id, project_id);
		} catch (SQLException e) {
			Log.e("RedmineFilterListItemAdapter::" + model.getClass().getName(),"getCount" , e);
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		try {
			return model.fetchItemByProject(connection_id, project_id, position, 1);
		} catch (SQLException e) {
			Log.e("RedmineFilterListItemAdapter::" + model.getClass().getName(),"getItem" , e);
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
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
