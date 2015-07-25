package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.ViewIssueList;

public class IssueListAdapter extends CursorAdapter {
	private static final String TAG = IssueListAdapter.class.getSimpleName();

	public IssueListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	class ViewHolder {
		public TextView textSubject;
		public TextView textTicketid;
		public TextView textDescription;
		public ImageView imageRecent;

		public TextView textStatus;
		public TextView textAssignedTo;
		public TextView textTracker;
		public TextView textPriority;
		public TextView textDateFrom;
		public TextView textDateTo;
		public TextView textVersion;
		public TextView textModified;
		public ProgressBar progressBar;
		public void setup(View view){
			textSubject = (TextView)view.findViewById(R.id.textSubject);
			textTicketid = (TextView)view.findViewById(R.id.textTicketid);
			textDescription = (TextView)view.findViewById(R.id.description);
			imageRecent = (ImageView)view.findViewById(R.id.imageRecent);

			textStatus = (TextView)view.findViewById(R.id.textStatus);
			textAssignedTo = (TextView)view.findViewById(R.id.textAssignedTo);
			progressBar = (ProgressBar)view.findViewById(R.id.progressissue);
			textTracker = (TextView)view.findViewById(R.id.textTracker);
			textPriority = (TextView)view.findViewById(R.id.textPriority);
			textDateFrom = (TextView)view.findViewById(R.id.textDateFrom);
			textDateTo = (TextView)view.findViewById(R.id.textDateTo);
			textVersion = (TextView)view.findViewById(R.id.textVersion);
			textModified = (TextView)view.findViewById(R.id.textModified);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = View.inflate(parent.getContext(), R.layout.listitem_issue, null);
		ViewHolder holder = new ViewHolder();
		holder.setup(view);
		view.setTag(holder);
		return view;
	}


	@Override
	public void bindView(final View view, Context context, final Cursor cursor) {
		final ViewHolder holder = (ViewHolder)view.getTag();
		CursorHelper.setDate(holder.textDateFrom, cursor, ViewIssueList.DATE_START);
		CursorHelper.setDate(holder.textDateTo, cursor, ViewIssueList.DATE_DUE);
		CursorHelper.setDateTimeSpan(holder.textModified, cursor, ViewIssueList.MODIFIED);
		CursorHelper.setText(holder.textSubject, cursor, ViewIssueList.SUBJECT);
		CursorHelper.setText(holder.textTracker, cursor, ViewIssueList.TRACKER_NAME);
		CursorHelper.setText(holder.textVersion, cursor, ViewIssueList.VERSION_NAME);
		CursorHelper.setText(holder.textDescription, cursor, ViewIssueList.DESCRIPTION);
		CursorHelper.setText(holder.textAssignedTo, cursor, ViewIssueList.ASSIGN_NAME);
		CursorHelper.setText(holder.textStatus, cursor, ViewIssueList.STATUS_NAME);
		CursorHelper.setText(holder.textPriority, cursor, ViewIssueList.PRIORITY_NAME);
		CursorHelper.setText(holder.textTicketid, cursor, "#%1$s", ViewIssueList.ISSUE_ID);
		holder.progressBar.setMax(100);
		CursorHelper.setProgress(holder.progressBar, cursor, ViewIssueList.PROGRESS, ViewIssueList.DONE_RATE);
	}

	public long getId(Cursor cursor) {
		int column_id = cursor.getColumnIndex(ViewIssueList.ID);
		return cursor.getLong(column_id);
	}

	public int getIssueId(Cursor cursor) {
		int column_id = cursor.getColumnIndex(ViewIssueList.ISSUE_ID);
		return cursor.getInt(column_id);
	}
	public int getConnectionId(Cursor cursor) {
		int column_id = cursor.getColumnIndex(ViewIssueList.CONNECTION);
		return cursor.getInt(column_id);
	}
}
