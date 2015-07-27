package jp.redmine.redmineclient.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.provider.Project;

public class ProjectListAdapter extends CursorAdapter {
	private static final String TAG = ProjectListAdapter.class.getSimpleName();

	/**
	 * The listener that receives notifications when an item is clicked.
	 */
	OnFavoriteClickListener mOnFavoriteClickListener;

	/**
	 * Interface definition for a callback to be invoked when an item in this
	 * AdapterView has been clicked.
	 */
	public interface OnFavoriteClickListener {
		/**
		 * Callback method to be invoked when an item in this AdapterView has
		 * been clicked.
		 * <p>
		 * Implementers can call getItemAtPosition(position) if they need
		 * to access the data associated with the selected item.
		 *
		 * @param view The view within the AdapterView that was clicked (this
		 *            will be a view provided by the adapter)
		 * @param position The position of the view in the adapter.
		 * @param cursor The position of the cursor in the adapter.
		 * @param b Rating bar status.
		 */
		void onItemClick(View view, int position, Cursor cursor, boolean b);
	}

	public void setOnFavoriteClickListener(OnFavoriteClickListener mOnItemClickListener) {
		this.mOnFavoriteClickListener = mOnItemClickListener;
	}

	public ProjectListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	class ViewHolder {
		public TextView textSubject;
		public CheckBox ratingBar;
		public void setup(View view){
			textSubject = (TextView)view.findViewById(R.id.textSubject);
			ratingBar = (CheckBox)view.findViewById(R.id.checkStar);
			ratingBar.setFocusable(false);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = View.inflate(parent.getContext(), R.layout.listitem_project, null);
		ViewHolder holder = new ViewHolder();
		holder.setup(view);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(final View view, Context context, final Cursor cursor) {
		final ViewHolder holder = (ViewHolder)view.getTag();
		int column_subject = cursor.getColumnIndex(RedmineProject.NAME);
		int column_ratingbar = cursor.getColumnIndex(RedmineProject.FAVORITE);
		holder.textSubject.setText(cursor.getString(column_subject));
		holder.ratingBar.setChecked(cursor.getInt(column_ratingbar) > 0);
		holder.ratingBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mOnFavoriteClickListener == null)
					return;
				mOnFavoriteClickListener.onItemClick(holder.ratingBar, cursor.getPosition(), cursor, isChecked);
			}
		});
	}

	public static int getConnectionId(Cursor cursor){
		int column_id = cursor.getColumnIndex(RedmineProject.CONNECTION);
		return cursor.getInt(column_id);
	}

	public static int getProjectId(Cursor cursor){
		int column_id = cursor.getColumnIndex(RedmineProject.PROJECT_ID);
		return cursor.getInt(column_id);
	}

	public static void updateFavorite(ContentResolver resolver, Cursor cursor, boolean b){
		ContentValues value = new ContentValues();
		value.put(RedmineProject.FAVORITE, b ? 1 : 0);
		int column_id = cursor.getColumnIndex(RedmineProject.ID);
		resolver.update(
				Uri.parse(Project.PROVIDER_BASE + "/id/" + String.valueOf(cursor.getInt(column_id)))
				, value, null, null
		);

	}

}
