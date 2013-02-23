package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.RedmineJournalModel;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.form.RedmineJournalListItemForm;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class RedmineJournalListAdapter extends BaseAdapter {

	private RedmineJournalModel model;
	protected int connection_id;
	protected long issue_id;



	public RedmineJournalListAdapter(RedmineJournalModel m, int connection, long issue){
		model = m;
		connection_id = connection;
		issue_id = issue;
	}

	@Override
	public int getCount() {
		try {
			return (int) model.countByIssue(connection_id, issue_id);
		} catch (SQLException e) {
			Log.e("RedmineJournalListAdapter::" + model.getClass().getName(),"getCount" , e);
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		try {
			return model.fetchItemByIssue(connection_id, issue_id,(long) position, 1);
		} catch (SQLException e) {
			Log.e("RedmineJournalListAdapter::" + model.getClass().getName(),"getItem" , e);
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		RedmineJournal rec = (RedmineJournal) getItem(position);
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
			convertView = infalInflater.inflate(R.layout.journalitem, null);
		}
		setupData(position, convertView);
		return convertView;
	}

	protected void setupData(int position,View convertView){
		if( convertView == null )
			return;
		RedmineJournalListItemForm form = new RedmineJournalListItemForm(convertView);
		RedmineJournal rec = null;
		if(convertView.getTag() == null || !(convertView.getTag() instanceof RedmineJournal)){
			rec = (RedmineJournal)getItem(position);
			convertView.setTag(rec);
		} else {
			rec = (RedmineJournal)convertView.getTag();
		}
		if(rec == null){
			Log.e("RedmineJournalListAdapter::" + model.getClass().getName(),"setupData");
			return;
		}
		form.setValue(rec);
	}


}
