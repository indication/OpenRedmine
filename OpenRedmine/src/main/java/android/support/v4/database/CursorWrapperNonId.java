package android.support.v4.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.text.TextUtils;

public class CursorWrapperNonId extends CursorWrapper {
	private String mIdName;
	/**
	 * Creates a cursor wrapper.
	 *
	 * @param cursor The underlying cursor to wrap.
	 */
	public CursorWrapperNonId(Cursor cursor) {
		super(cursor);
	}

	public CursorWrapperNonId(Cursor cursor, String idName) {
		super(cursor);
		mIdName = idName;
	}

	@Override
	public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
		if(TextUtils.isEmpty(mIdName) || !"_id".equals(columnName))
			return super.getColumnIndexOrThrow(columnName);
		return super.getColumnIndexOrThrow(mIdName);
	}
}
