package jp.redmine.redmineclient.form;

import jp.redmine.redmineclient.entity.IMasterRecord;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
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
		checkbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CompoundButton buttonView = (CompoundButton) v;
				if(buttonView.isChecked()){
					if(buttonView.getTag() != null){
						int pos = (Integer)buttonView.getTag();

						list.clearChoices();
						list.setItemChecked(pos, true);
					}
				} else {
					if(list.getCheckedItemPosition() >= 0){
						int pos = list.getCheckedItemPosition();
						buttonView.setTag(pos);
						list.clearChoices();
						list.setItemChecked(0, true);
					} else {
						buttonView.setTag(null);
					}
				}


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


}
