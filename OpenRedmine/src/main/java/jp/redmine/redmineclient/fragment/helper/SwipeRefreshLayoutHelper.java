package jp.redmine.redmineclient.fragment.helper;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;

public class SwipeRefreshLayoutHelper {
	public static void setRefreshingPost(final SwipeRefreshLayout layout, final boolean isRefreshing){
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			return;
		if (layout == null)
			return;
		layout.post(new Runnable() {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void run() {
				layout.setRefreshing(isRefreshing);
			}
		});
	}
	public static void setEvent(SwipeRefreshLayout layout, SwipeRefreshLayout.OnRefreshListener listener){
		if (layout == null)
			return;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			return;
		layout.setOnRefreshListener(listener);
	}
	public static void setRefreshing(SwipeRefreshLayout layout, boolean isRefreshing){
		if (layout == null)
			return;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			return;
		layout.setRefreshing(isRefreshing);
	}
	public static void setRefreshing(SwipeRefreshLayout layout, boolean isRefreshing, boolean enabled){
		if (layout == null)
			return;
		layout.setEnabled(enabled);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			return;
		layout.setRefreshing(isRefreshing);
	}
}
