package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProjectContract;
import jp.redmine.redmineclient.form.helper.HtmlHelper;
import jp.redmine.redmineclient.model.ConnectionModel;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class FavoriteProjectListAdapter extends CursorAdapter implements StickyListHeadersAdapter {
	public FavoriteProjectListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	class ViewHolder {
		public TextView text;
		public void setup(View view){
			text = (TextView)view.findViewById(android.R.id.text1);
		}
	}
	@Override
	public View getHeaderView(int i, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(parent.getContext(), R.layout.listheader_connection, null);
		}
		if(convertView == null)
			return null;

		RedmineConnection connection = ConnectionModel.getConnectionItem(mContext.getContentResolver(), (int) getHeaderId(i));
		TextView text = (TextView)convertView.findViewById(R.id.name);
		if(text != null)
			text.setText((TextUtils.isEmpty(connection.getName())) ? "" : connection.getName());
		//fix background to hide transparent headers
		convertView.setBackgroundColor(HtmlHelper.getBackgroundColor(convertView.getContext()));
		return convertView;
	}

	@Override
	public long getHeaderId(int i) {
		Cursor cursor = (Cursor)getItem(i);
		return cursor == null ? 0 : cursor.getInt(cursor.getColumnIndex(RedmineProjectContract.CONNECTION_ID));
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
		final ViewHolder holder = (ViewHolder)view.getTag();
		int column_subject = cursor.getColumnIndex(RedmineProjectContract.NAME);
		holder.text.setText(cursor.getString(column_subject));
	}


}
