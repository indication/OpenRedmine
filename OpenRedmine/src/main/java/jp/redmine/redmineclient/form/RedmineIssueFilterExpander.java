package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.entity.IMasterRecord;
import android.app.Activity;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class RedmineIssueFilterExpander {
	public BaseAdapter adapter;
	public ListView list;
	public void setup(Activity activity,int listid){
		list = activity.findViewById(listid);

	}
	public void setupEvent(){

		if(adapter != null){
			list.setAdapter(adapter);
		}
	}
	public IMasterRecord getSelectedItem(){
		int pos = list.getCheckedItemPosition();
		if(pos < 0 || adapter == null)
			return null;
		return (IMasterRecord) adapter.getItem(pos);
	}
	public void selectItem(IMasterRecord rec){
		list.clearChoices();
		if(rec == null || adapter == null){
			list.setItemChecked(0, true);
			return;
		}
		for(int position = 0; position < adapter.getCount(); position++){
			Object local = adapter.getItem(position);
			if(! (local instanceof IMasterRecord)){
				continue;
			}
			IMasterRecord posrec = (IMasterRecord)local;
			if(posrec.getId() != rec.getId()){
				continue;
			}
			list.setItemChecked(position, true);
		}
	}
	public void refresh(){
		if(adapter == null)
			return;
		adapter.notifyDataSetInvalidated();
		adapter.notifyDataSetChanged();
	}


}
