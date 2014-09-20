package com.j256.ormlite.android.apptools;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.Context;
import android.os.Build;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;

public abstract class OrmLiteContentProvider<H extends OrmLiteSqliteOpenHelper> extends ContentProvider {
	private volatile H helper;
	private volatile boolean created = false;
	private volatile boolean destroyed = false;
	private static Logger logger = LoggerFactory.getLogger(OrmLiteContentProvider.class);

	/**
	 * Get a helper for this action.
	 */
	public H getHelper() {
		if (helper == null) {
			if (!created) {
				throw new IllegalStateException("A call has not been made to onCreate() yet so the helper is null");
			} else if (destroyed) {
				throw new IllegalStateException(
						"A call to onDestroy has already been made and the helper cannot be used after that point");
			} else {
				throw new IllegalStateException("Helper is null for some unknown reason");
			}
		} else {
			return helper;
		}
	}

	/**
	 * Get a connection source for this action.
	 */
	public ConnectionSource getConnectionSource() {
		return getHelper().getConnectionSource();
	}

	@Override
	public boolean onCreate() {
		if (helper == null) {
			helper = getHelperInternal(getContext());
			created = true;
		}
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void shutdown() {
		releaseHelper(helper);
		destroyed = true;
		super.shutdown();
	}

	/**
	 * This is called internally by the class to populate the helper object instance. This should not be called directly
	 * by client code unless you know what you are doing. Use {@link #getHelper()} to get a helper instance. If you are
	 * managing your own helper creation, override this method to supply this activity with a helper instance.
	 *
	 * <p>
	 * <b> NOTE: </b> If you override this method, you most likely will need to override the
	 * {@link #releaseHelper(OrmLiteSqliteOpenHelper)} method as well.
	 * </p>
	 */
	protected H getHelperInternal(Context context) {
		@SuppressWarnings({ "unchecked", "deprecation" })
		H newHelper = (H) OpenHelperManager.getHelper(context,getOrmClass());
		logger.trace("{}: got new helper {} from OpenHelperManager", this, newHelper);
		return newHelper;
	}

	abstract protected Class<H> getOrmClass();

	/**
	 * Release the helper instance created in {@link #getHelperInternal(Context)}. You most likely will not need to call
	 * this directly since {@link #shutdown()} does it for you.
	 *
	 * <p>
	 * <b> NOTE: </b> If you override this method, you most likely will need to override the
	 * {@link #getHelperInternal(Context)} method as well.
	 * </p>
	 */
	protected void releaseHelper(H helper) {
		OpenHelperManager.releaseHelper();
		logger.trace("{}: helper {} was released, set to null", this, helper);
		this.helper = null;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
	}
}
