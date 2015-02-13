package com.j256.ormlite.android.apptools;

import android.support.v4.app.ListFragment;

import com.j256.ormlite.support.ConnectionSource;

/**
 * Base class to use for fragment in Android.
 *
 * You can simply call {@link #getHelper()} to get your helper class, or {@link #getConnectionSource()} to get a
 * {@link ConnectionSource}.
 *
 * The method {@link #getHelper()} assumes you are using the default helper factory -- see {@link OpenHelperManager}. If
 * not, you'll need to provide your own helper instances which will need to implement a reference counting scheme. This
 * method will only be called if you use the database, and only called once for this activity's life-cycle. 'close' will
 * also be called once for each call to createInstance.
 *
 * @author graywatson, kevingalligan
 */
public abstract class OrmLiteListFragment<H extends OrmLiteSqliteOpenHelper> extends ListFragment {

    /**
     * Get a helper for this action.
     */
    public H getHelper() {
        if(getActivity() instanceof OrmLiteFragmentActivity<?>){
			return ((OrmLiteFragmentActivity<H>)getActivity()).getHelper();
		} else {
			return null;
		}
    }

    /**
     * Get a connection source for this action.
     */
    public ConnectionSource getConnectionSource() {
        return getHelper().getConnectionSource();
    }

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
	}
}