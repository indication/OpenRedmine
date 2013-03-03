package jp.redmine.redmineclient.form;

import android.widget.BaseAdapter;
import android.widget.ListView;

public class RedmineBaseAdapterListFormHelper<A extends BaseAdapter> extends RedmineBaseListFormHelper {
	public A adapter;
	public void setAdapter(A ad){
		adapter = ad;
		if(list != null){
			list.setAdapter(ad);
		}
	}
	@Override
	public void setList(ListView listview) {
		super.setList(listview);
		if(adapter != null){
			list.setAdapter(adapter);
		}
	}
	public void refresh(){
		refresh(true);
	}
	public void refresh(boolean isRestoreState){
		if(adapter == null)
			return;
		adapter.notifyDataSetInvalidated();
		adapter.notifyDataSetChanged();
		if(isRestoreState)
			restoreScrollState();
	}
}

