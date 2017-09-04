package jp.redmine.redmineclient.fragment.form;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;

public class IssueFilterHeaderForm {
	TableLayout layoutTable;

	public IssueFilterHeaderForm(View view){
		setup(view);
	}

	public void setup(View view) {
		layoutTable = (TableLayout) view.findViewById(R.id.layoutTable);
	}

	public void setValue(RedmineFilter item) {
		layoutTable.removeAllViewsInLayout();
		if(item == null)
			return;
		addRow(item.getProject(), R.string.ticket_project);
		addRow(item.getCategory(), R.string.ticket_category);
		addRow(item.getAuthor(), R.string.ticket_author);
		addRow(item.getAssigned(), R.string.ticket_assigned);
		addRow(item.getPriority(), R.string.ticket_priority);
		addRow(item.getStatus(), R.string.ticket_status);
		addRow(item.getTracker(), R.string.ticket_tracker);
		addRow(item.getVersion(), R.string.ticket_version);
		addRow(item.isClosed(), R.string.ticket_status, R.string.ticket_closed, R.string.ticket_open);

		if (!TextUtils.isEmpty(item.getSort())) {
			RedmineFilterSortItem sort = RedmineFilterSortItem.setupFilter(new RedmineFilterSortItem(), item.getSort());
			String sort_name = sort == null ? "" : layoutTable.getContext().getString(sort.getResource());
			layoutTable.addView(generateRow(layoutTable.getContext(), sort_name, R.string.ticket_sort));
		}
	}

	protected void addRow(IMasterRecord changes, int title_id) {
		if (changes == null)
			return;
		layoutTable.addView(generateRow(layoutTable.getContext(), changes.getName(), title_id));
	}
	protected void addRow(Boolean changes, int title_id, int positive, int negative) {
		if (changes == null)
			return;
		String value = layoutTable.getContext().getString(changes ? positive : negative);
		layoutTable.addView(generateRow(layoutTable.getContext(), value, title_id));
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
