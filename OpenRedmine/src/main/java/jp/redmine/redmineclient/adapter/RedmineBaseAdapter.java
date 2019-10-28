package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.external.lib.LRUCache;

abstract class RedmineBaseAdapter<T> extends BaseAdapter implements LRUCache.IFetchObject<Integer> {
	private static final String TAG = "RedmineBaseAdapter";
	protected LRUCache<Integer,T> cache = new LRUCache<>(20);
	protected abstract int getItemViewId();
	protected abstract void setupView(View view,T data);
	protected abstract int getDbCount() throws SQLException;
	protected abstract T getDbItem(int position) throws SQLException;
	protected abstract long getDbItemId(T item);
	/**
	 * Adding item on the background, it causes crash on notifyDataSetChanged.
	 * To avoid crash, store count before notifyDataSetChanged and return count in the getCount
	 * inherit from http://www.mumei-himazin.info/blog/?p=96
	 */
	private int count = 0;
	/**
	 * Bless ListView the data has been changed.
	 * This method catches SQLException.
	 */
	@Override
	public void notifyDataSetChanged() {
		cache.clear();
        if(isValidParameter()){
            try {
                count = getDbCount();
            } catch (SQLException e) {
                Log.e(TAG,"getDbCount" , e);
                count = 0;
            }
        }
		super.notifyDataSetChanged();
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
        try {
			return getDbItem(position);
		} catch (SQLException e) {
			Log.e(TAG,"getDbItem" , e);
			return null;
		}
	}

	/**
	 * Get count from database
	 * Before Call this, call notifyDataSetChanged is needed.
	 * @return 0 or item count
	 */
	@Override
	public int getCount() {
		return count;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView != null) {
			if( convertView.getTag() == null
					|| !(convertView.getTag() instanceof Integer)
					|| ((Integer)convertView.getTag()) != getItemViewId()){
				convertView = null;
			}
		}
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = getItemView(infalInflater, parent);
			convertView.setTag(getItemViewId());
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
}
