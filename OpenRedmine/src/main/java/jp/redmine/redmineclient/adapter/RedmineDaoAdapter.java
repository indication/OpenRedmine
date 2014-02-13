package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

import jp.redmine.redmineclient.external.lib.LRUCache;

public abstract class RedmineDaoAdapter<T, ID, H extends OrmLiteSqliteOpenHelper> extends BaseAdapter implements LRUCache.IFetchObject<Integer> {
	private static final String TAG = RedmineDaoAdapter.class.getSimpleName();
	protected LRUCache<Integer,T> cache = new LRUCache<Integer,T>(10);
	protected final LayoutInflater infrator;
	protected Dao<T, ID> dao;
	private AndroidDatabaseResults dbResults;
	protected abstract long getDbItemId(T item);
	protected abstract int getItemViewId();
	protected abstract void setupView(View view,T data);
	protected abstract QueryBuilder<T, ID> getQueryBuilder() throws SQLException;

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
	@Override
	public void notifyDataSetInvalidated() {
		close();
		super.notifyDataSetInvalidated();
	}

	protected void close(){
		cache.clear();
		if(dbResults != null){
			dbResults.closeQuietly();
			dbResults = null;
		}
	}

	@Override
	public long getItemId(int position) {
		return getDbItemId(getItemWithCache(position));
	}
	/**
	 * Get item from database via cache
	 * This method catches SQLException.
	 * @return null or item
	 * @deprecated this method is called from BaseAdapter only
	 */
	@Override
	public Object getItem(int position) {
		return getItemWithCache(position);
	}

	/**
	 * Get item from database via cache
	 * This method catches SQLException.
	 * @return null or item
	 */
	protected T getItemWithCache(int position) {
		return cache.getValue(position, this);
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
	public Object getItem(Integer position) {
		if(!isValidParameter())
            return null;
		return getDbItem(position);
	}

	private T getDbItem(int position){
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
		if (convertView == null) {
			convertView = getItemView(infrator);
		}
		if(convertView != null){
			T rec = getItemWithCache(position);
			if(rec == null){
				Log.e(TAG,"getValue returns data is null");
			} else {
				setupView(convertView,rec);
			}
		}
		return convertView;
	}
	protected View getItemView(LayoutInflater infalInflater){
		return infalInflater.inflate(getItemViewId(), null);
	}
	
	@Override
	public int getItemViewType(int pos) {
		return getItemViewId();
	}

    public boolean isValidParameter(){
        return true;
    }
}
