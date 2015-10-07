package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.redmine.redmineclient.entity.RedmineProjectCategory;

public class CategoryListAdapter extends CursorAdapter {
	private static final String TAG = IssueListAdapter.class.getSimpleName();

	public CategoryListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	class ViewHolder {
		public TextView textText;
		public void setup(View view){
			textText = (TextView)view.findViewById(android.R.id.text1);
		}
	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = View.inflate(parent.getContext(), android.R.layout.simple_list_item_1, null);
		ViewHolder holder = new ViewHolder();
		holder.setup(view);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(final View view, Context context, final Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		CursorHelper.setText(holder.textText, cursor, RedmineProjectCategory.NAME);
	}


	public static long getId(Cursor cursor){
		int column_id = cursor.getColumnIndex(RedmineProjectCategory.ID);
		return cursor.getLong(column_id);
	}

}
