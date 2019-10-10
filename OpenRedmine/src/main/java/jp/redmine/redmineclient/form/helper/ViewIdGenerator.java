package jp.redmine.redmineclient.form.helper;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

public class ViewIdGenerator {

	private AtomicInteger seq = new AtomicInteger(1);

	public int getViewId(View checkView) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			return getAndroidViewId();
		else
			return getCustomViewId(checkView);
	}

	private int getCustomViewId(View checkView){
		for(;;){
			int customid = seq.incrementAndGet();
			if(customid > 0x00FFFFFF) {
				customid = 1;
				seq.set(customid);
			}
			if(checkView == null || checkView.findViewById(customid) == null){
				return customid;
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	protected int getAndroidViewId(){
		return View.generateViewId();
	}

}
