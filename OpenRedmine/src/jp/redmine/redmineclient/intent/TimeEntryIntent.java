package jp.redmine.redmineclient.intent;

import android.content.Context;
import android.content.Intent;

public class TimeEntryIntent extends IssueIntent {
	public TimeEntryIntent(Intent intent) {
		super(intent);
	}
	public TimeEntryIntent(Context applicationContext,Class<?> cls) {
		super(applicationContext,cls);
	}
	public static final String TIMEENTRY_ID = "TIMEENTRY";

	public void setTimeEntryId(int id){
		intent.putExtra(TIMEENTRY_ID,id);
	}
	public int getTimeEntryId(){
		return intent.getIntExtra(TIMEENTRY_ID, -1);
	}
}
