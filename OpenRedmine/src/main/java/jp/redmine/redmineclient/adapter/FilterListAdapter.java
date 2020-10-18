package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.IMasterModel;
import jp.redmine.redmineclient.entity.DummySelection;
import jp.redmine.redmineclient.entity.IMasterRecord;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class FilterListAdapter extends RedmineBaseAdapter<IMasterRecord> {

	private IMasterModel<? extends IMasterRecord> model;
	protected Integer connection_id;
	protected Long project_id;
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

	public FilterListAdapter(IMasterModel<? extends IMasterRecord> m){
		model = m;
	}

	public void setupParameter(int connection, long project){
		setupParameter(connection, project, true);
	}

	public void setupParameter(int connection, long project, boolean isAddNone){
		addNone = isAddNone;
		connection_id = connection;
		project_id = project;
	}

    @Override
	public boolean isValidParameter(){
		if(project_id == null || connection_id == null)
			return false;
		else
			return true;
	}

	@Override
	protected int getItemViewId() {
		return android.R.layout.simple_list_item_single_choice;
	}
	@Override
	protected void setupView(View view, IMasterRecord data) {
		TextView text = view.findViewById(android.R.id.text1);
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
