package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Locale;

import jp.redmine.redmineclient.R;

abstract class RedmineDaoAdapter<T, ID, H extends OrmLiteSqliteOpenHelper>
		extends BaseAdapter
		implements Filterable
{
	private static final String TAG = RedmineDaoAdapter.class.getSimpleName();
	protected final LayoutInflater infrator;
	protected Dao<T, ID> dao;
	private AndroidDatabaseResults dbResults;
	protected abstract long getDbItemId(T item);
	protected abstract int getItemViewId();
	protected abstract void setupView(View view,T data);
	protected abstract QueryBuilder<T, ID> getQueryBuilder() throws SQLException;
	protected QueryBuilder<T, ID> getSearchQueryBuilder(String search) throws SQLException{
		return null;
	}

	public RedmineDaoAdapter(H helper, Context context, Class<T> daoClass){
		super();
		infrator = LayoutInflater.from(context);
		try {
			dao = helper.getDao(daoClass);
		} catch (SQLException e) {
			Log.e(TAG,TAG,e);
		}
	}
	/**
	 * Bless ListView the data has been changed.
	 * This method catches SQLException.
	 */
	@Override
	public void notifyDataSetChanged() {
		close();
        if(isValidParameter()){
            try {
				dbResults =  (AndroidDatabaseResults) dao.iterator(getQueryBuilder().prepare()).getRawResults();
            } catch (SQLException e) {
                Log.e(TAG,"getDbCount" , e);

            }
        }
		super.notifyDataSetChanged();
	}
	private void notifySuperDataSetChanged(){
		super.notifyDataSetChanged();
	}
	@Override
	public void notifyDataSetInvalidated() {
		close();
		super.notifyDataSetInvalidated();
	}

	protected void close(){
		if(dbResults != null){
			dbResults.closeQuietly();
			dbResults = null;
		}
	}

	@Override
	public long getItemId(int position) {
		return getDbItemId((T)getItem(position));
	}

	/**
	 * Get item from database
	 * Called from LRUCache class.
	 * This method catches SQLException.
	 * @param position parameter
	 * @return null or item
	 * @deprecated this method is called from IFetchObject only
	 */
	@Override
	public Object getItem(int position) {
		if(!isValidParameter())
            return null;
		return getDbItem(position);
	}

	protected T getDbItem(int position){
		if(dbResults == null)
			return null;
		dbResults.moveAbsolute(position);
		try {
			return dao.mapSelectStarRow(dbResults);
		} catch (SQLException e) {
			Log.e(TAG, "getItem", e);
		}
		return null;
	}

	/**
	 * Get count from database
	 * Before Call this, call notifyDataSetChanged is needed.
	 * @return 0 or item count
	 */
	@Override
	public int getCount() {
		return dbResults == null ? 0 : dbResults.getCount();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Check view id when reuse views
		if(convertView != null){
			Object tag = convertView.getTag(R.id.list);
			if(tag != null && tag instanceof Integer && (Integer)tag != getItemViewId())
				convertView = null;
		}
		if (convertView == null) {
			convertView = getItemView(infrator, parent);
			convertView.setTag(R.id.list, getItemViewId());
		}
		if(convertView != null){
			T rec = (T)getItem(position);
			if(rec == null){
				Log.e(TAG,"getValue returns data is null");
			} else {
				setupView(convertView,rec);
			}
		}
		return convertView;
	}
	protected View getItemView(LayoutInflater infalInflater, ViewGroup parent){
		return infalInflater.inflate(getItemViewId(), parent, false);
	}
	
	@Override
	public int getItemViewType(int pos) {
		return getItemViewId();
	}

    public boolean isValidParameter(){
        return true;
    }

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence charSequence) {
				FilterResults result = new FilterResults();
				if(isValidParameter()){
					try {
						QueryBuilder<T, ID> query;
						if(TextUtils.isEmpty(charSequence)){
							query = getQueryBuilder();
						} else {
							String str = charSequence.toString().toLowerCase(Locale.getDefault()).trim();
							query = getSearchQueryBuilder(str);
						}
						if(query == null)
							return result;
						AndroidDatabaseResults ret = (AndroidDatabaseResults)dao.iterator(query.prepare()).getRawResults();
						result.values =  ret;
						result.count = ret.getCount();
					} catch (SQLException e) {
						Log.e(TAG,"performFiltering" , e);
					}
				}
				return result;
			}

			@Override
			protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
				if(filterResults.count == 0){
					notifyDataSetInvalidated();
				} else {
					close();
					dbResults = (AndroidDatabaseResults) filterResults.values;
					notifySuperDataSetChanged();
				}
			}
		};
	}
}
