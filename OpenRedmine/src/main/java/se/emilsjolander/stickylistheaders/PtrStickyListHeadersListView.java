package se.emilsjolander.stickylistheaders;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.ViewDelegate;

/**
 * Inherit from http://stackoverflow.com/questions/20143008/is-it-possible-to-merge-stickylistviewheader-with-crisbanes-pulltorefresh
 * @author Helden
 */
public class PtrStickyListHeadersListView extends StickyListHeadersListView
		implements ViewDelegate {

	public PtrStickyListHeadersListView(Context context) {
		super(context);
	}

	public PtrStickyListHeadersListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PtrStickyListHeadersListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isReadyForPull(View view, float v, float v2) {
		View childView = getWrappedList().getChildAt(0);
		int top = (childView == null) ? 0 : childView.getTop();
		return top >= 0;
	}
}
