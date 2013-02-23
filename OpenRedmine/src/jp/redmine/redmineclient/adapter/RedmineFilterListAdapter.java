package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.IMasterModel;
import jp.redmine.redmineclient.entity.DummySelection;
import jp.redmine.redmineclient.entity.IMasterRecord;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class RedmineFilterListAdapter extends RedmineBaseAdapter<IMasterRecord> {

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
	protected View getItemView(LayoutInflater infalInflater) {
		// TODO 自動生成されたメソッド・スタブ
		return infalInflater.inflate(android.R.layout.simple_list_item_single_choice, null);
	}
	@Override
	protected void setupView(View view, IMasterRecord data) {
		TextView text = (TextView)view.findViewById(android.R.id.text1);
		text.setText(data.getName());
	}
	@Override
	protected int getDbCount() throws SQLException {
		int count = addNone ? 1 : 0;
		count += (int) model.countByProject(connection_id, project_id);
		return count;
	}
	@Override
	protected IMasterRecord getDbItem(int position) throws SQLException {
		int realpos = position - (addNone ? 1 : 0);
		switch(position){
		case 0:
			if(addNone){
				return getDummyItem();
			}
		default:
		}
		return model.fetchItemByProject(connection_id, project_id, realpos, 1);
	}
	@Override
	protected long getDbItemId(IMasterRecord item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}


}
