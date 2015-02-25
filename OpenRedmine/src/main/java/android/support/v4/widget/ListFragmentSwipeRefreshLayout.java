package android.support.v4.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.lang.ref.WeakReference;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {

	private WeakReference<View> mTarget;

	protected View getTarget(){
		if (mTarget == null || mTarget.get() == null) {
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				mTarget = new WeakReference<View>(child);
				break;
			}
		}
		if (mTarget == null || mTarget.get() == null) {
			return null;
		} else {
			return mTarget.get();
		}
	}
	public ListFragmentSwipeRefreshLayout(Context context) {
		super(context);
	}

	public ListFragmentSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	static public class ViewRefreshLayout {
		public SwipeRefreshLayout layout;
		public View parent;
	}

	static public ViewRefreshLayout inject(ViewGroup container, View fragment) {
		ViewRefreshLayout result = new ViewRefreshLayout();
		ListFragmentSwipeRefreshLayout layout = new ListFragmentSwipeRefreshLayout(container.getContext());
		result.layout = layout;
		View list = fragment.findViewById(android.R.id.list);
		ViewGroup parent = null;
		if(list == null) {
			list = fragment;
			result.parent = layout;
		} else {
			result.parent = fragment;
		}
		parent = (ViewGroup) list.getParent();
		int place = 0;
		if (parent != null) {
			for(int i = 0; i < parent.getChildCount(); i++){
				if(parent.getChildAt(i).getId() == list.getId()){
					place = i;
					break;
				}
			}
			parent.removeView(list);
			parent.addView(layout,place);
		}
		layout.addView(list
				, ViewGroup.LayoutParams.MATCH_PARENT
				, ViewGroup.LayoutParams.MATCH_PARENT
		);
		return result;
	}

	@Override
	public boolean canChildScrollUp() {
		View view = getTarget();
		if (view instanceof StickyListHeadersListView){
			ListView listView = ((StickyListHeadersListView) view).getWrappedList();
			if (android.os.Build.VERSION.SDK_INT >= 14) {
				// For ICS and above we can call canScrollVertically() to determine this
				return ViewCompat.canScrollVertically(listView, -1);
			} else {
				// Pre-ICS we need to manually check the first visible item and the child view's top
				// value
				return listView.getChildCount() > 0 &&
						(listView.getFirstVisiblePosition() > 0
								|| listView.getChildAt(0).getTop() < listView.getPaddingTop());
			}
		} else {
			return super.canChildScrollUp();
		}
	}
}
