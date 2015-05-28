package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.redmine.redmineclient.entity.RedmineConnection;

public class ConnectionListAdapter extends CursorAdapter {
	private static final String TAG = ConnectionListAdapter.class.getSimpleName();

	public ConnectionListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	class ViewHolder {
		public TextView textSubject;
		public void setup(View view){
			textSubject = (TextView)view.findViewById(android.R.id.text1);
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
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder)view.getTag();
		int column_id = cursor.getColumnIndex("name");
		holder.textSubject.setText(cursor.getString(column_id));
	}

	public int getId(Cursor cursor){
		int column_id = cursor.getColumnIndex(RedmineConnection.ID);
		return cursor.getInt(column_id);
	}
}
