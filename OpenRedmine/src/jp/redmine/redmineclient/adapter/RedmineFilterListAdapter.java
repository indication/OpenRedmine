package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.model.FilterListAdapterModel;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

public class RedmineFilterListAdapter extends BaseExpandableListAdapter {

	private FilterListAdapterModel adapter;
	public RedmineFilterListAdapter(FilterListAdapterModel adapt){
		adapter = adapt;

	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		try {
			return adapter.getChild(groupPosition, childPosition);
		} catch (SQLException e) {
			Log.e("RedmineFilterListAdapter","getChild",e);
		}
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return (getGroupId(groupPosition) | childPosition) ;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		RadioButton radio = (RadioButton)convertView;
		if( convertView == null ){
			radio = new RadioButton(parent.getContext());
			convertView = radio;
		}
		IMasterRecord rec = (IMasterRecord)getChild(groupPosition, childPosition);
		if(rec!=null)
			radio.setText(rec.getName());
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		try {
			return adapter.getChildCount(groupPosition);
		} catch (SQLException e) {
			Log.e("RedmineFilterListAdapter","getChildrenCount",e);
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return adapter.getGroup(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return adapter.getGroupCount();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition << 16;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		/*
		CheckBox check = (CheckBox)convertView;
		if( convertView == null ){
			check = new CheckBox(parent.getContext());
			convertView = check;
		}
		*/
		TextView check = (TextView)convertView;
		if( convertView == null ){
			check = new TextView(parent.getContext());
			convertView = check;
		}

		String rec = (String)getGroup(groupPosition);
		if(rec!=null)
			check.setText(rec);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
