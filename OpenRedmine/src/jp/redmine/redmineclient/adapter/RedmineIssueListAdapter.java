package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.external.lib.LRUCache;
import jp.redmine.redmineclient.form.RedmineIssueListItemForm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class RedmineIssueListAdapter extends BaseAdapter implements LRUCache.IFetchObject<Integer> {
	private RedmineIssueModel model;
	protected int connection_id;
	protected long project_id;
	protected LRUCache<Integer,RedmineIssue> cache = new LRUCache<Integer,RedmineIssue>(20);
	public RedmineIssueListAdapter(RedmineIssueModel m, int connection, long project) {
		super();
		model = m;
		connection_id = connection;
		project_id = project;
	}

	@Override
	public int getCount() {
		try {
			return (int) model.countByProject(connection_id, project_id);
		} catch (SQLException e) {
			Log.e("RedmineIssueListAdapter::" + model.getClass().getName(),"getCount" , e);
		}
		return 0;
	}
	@Override
	public Object getItem(Integer position) {
		return getItem(position);
	}

	@Override
	public Object getItem(int position) {
		try {
			Log.d("RedmineIssueListAdapter","position:" + position);
			return model.fetchItemByProject(connection_id, project_id,(long) position, 1L);
		} catch (SQLException e) {
			Log.e("RedmineIssueListAdapter::" + model.getClass().getName(),"getItem" , e);
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		RedmineIssue rec = (RedmineIssue) getItem(position);
		if(rec == null){
			return -1;
		} else {
			return rec.getId();
		}
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.issueitem, null);
		}
		setupData(position, convertView);
		return convertView;
	}

	protected void setupData(int position,View convertView){
		if( convertView == null )
			return;
		RedmineIssueListItemForm form = new RedmineIssueListItemForm(convertView);
		RedmineIssue rec = cache.getValue(position, this);
		if(rec == null){
			Log.e("RedmineIssueListAdapter::" + model.getClass().getName(),"setupData");
			return;
		}
		form.setValue(rec);
	}

}
