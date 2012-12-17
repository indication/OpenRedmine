package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.entity.IMasterRecord;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class RedmineIssueFilterExpander {
	public CheckBox checkbox;
	public CheckBox expander;
	public ListAdapter adapter;
	public ListView list;
	public void setup(Activity activity,int checkid,int expanderid,int listid){
		checkbox = (CheckBox)activity.findViewById(checkid);
		expander = (CheckBox)activity.findViewById(expanderid);
		list = (ListView)activity.findViewById(listid);

	}
	public void setupEvent(){
		expander.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				list.setVisibility(isChecked ? View.VISIBLE : View.GONE);
			}
		});
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterview, View parent,
					int position, long id) {
				/**
				 * Dummy item must returns under 0
				 * @seealso:RedmineFilterListAdapter
				 */
				if(id > 0){
					checkbox.setChecked(true);
				} else {
					checkbox.setChecked(false);
				}
			}
		});

		if(adapter != null){
			list.setAdapter(adapter);
		}
	}
	public void selectItem(IMasterRecord rec){
		list.clearChoices();
		if(rec == null){
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

}
