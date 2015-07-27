package jp.redmine.redmineclient.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.helper.HtmlHelper;
import jp.redmine.redmineclient.model.ConnectionModel;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class FavoriteProjectListAdapter extends ProjectListAdapter implements StickyListHeadersAdapter {
	private ContentResolver resolver;

	public FavoriteProjectListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		resolver = context.getContentResolver();
	}
	@Override
	public View getHeaderView(int i, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(parent.getContext(),R.layout.listheader_connection, null);
		}
		if(convertView == null)
			return null;
		RedmineConnection connection = ConnectionModel.getConnectionItem(resolver, (int)getHeaderId(i));
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
		int column_id = cursor.getColumnIndex(RedmineProject.CONNECTION);
		return cursor.getInt(column_id);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);
		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.ratingBar.setVisibility(View.GONE);
	}
}
