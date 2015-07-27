package jp.redmine.redmineclient.fragment.form;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.IMasterRecord;

public class IssueFilterHeaderForm {
	TableLayout layoutTable;

	public IssueFilterHeaderForm(View view){
		setup(view);
	}

	public void setup(View view) {
		layoutTable = (TableLayout) view.findViewById(R.id.layoutTable);
	}
	public void setValue(Cursor item) {
		layoutTable.removeAllViewsInLayout();
		if (item == null)
			return;
		if(item.getCount() < 1)
			return;
		int col_res_id = item.getColumnIndex("res_id");
		int col_res_name = item.getColumnIndex("res_name");
		int col_name = item.getColumnIndex("name");
		while(item.moveToNext()) {
			Integer res_id = item.getInt(col_res_id);
			Integer res_name = item.getInt(col_res_name);
			String name = (res_name == 0) ? item.getString(col_name) : layoutTable.getContext().getString(res_name);
			layoutTable.addView(generateRow(layoutTable.getContext(), name, res_id));
		}
		item.close();
	}

	protected void addRow(IMasterRecord changes, int title_id) {
		if (changes == null)
			return;
		layoutTable.addView(generateRow(layoutTable.getContext(), changes.getName(), title_id));
	}

	static protected TableRow generateRow(Context context, String setting, int title_id) {
		TableRow row = new TableRow(context);

		// add name
		TextView title = new TextView(context);
		title.setText(context.getString(title_id));
		title.setTypeface(Typeface.DEFAULT_BOLD);
		row.addView(title);

		// add value
		TextView value = new TextView(context);
		value.setText(setting);
		row.addView(value);

		return row;
	}
}
