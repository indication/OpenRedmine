package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;
import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;
import android.view.View;
import android.widget.TextView;

public class FilterSortListAdapter extends RedmineBaseAdapter<RedmineFilterSortItem> {

	protected List<RedmineFilterSortItem> items = RedmineFilterSortItem.getFilters(true);

	@Override
	protected int getItemViewId() {
		return android.R.layout.simple_list_item_single_choice;
	}
	@Override
	protected void setupView(View view, RedmineFilterSortItem item) {
		TextView text = (TextView)view.findViewById(android.R.id.text1);
		text.setText(view.getContext().getString(item.getResource())
				.concat("\t").concat(view.getContext().getString(
						item.isAscending() ? R.string.ascending : R.string.descending
						)));
	}
	@Override
	protected int getDbCount() throws SQLException {
		return items.size();
	}
	@Override
	protected RedmineFilterSortItem getDbItem(int position) throws SQLException {
		return items.get(position);
	}
	@Override
	protected long getDbItemId(RedmineFilterSortItem item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}


}
