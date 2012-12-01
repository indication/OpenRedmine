package jp.redmine.redmineclient.intent;

import android.content.Context;
import android.content.Intent;

public class IssueIntent extends ConnectionIntent {
	public IssueIntent(Intent intent) {
		super(intent);
	}
	public IssueIntent(Context applicationContext,Class<?> cls) {
		super(applicationContext,cls);
	}
	public static final String ISSUE_ID = "ISSUEID";

	public void setIssueId(int id){
		intent.putExtra(ISSUE_ID,id);
	}
	public int getIssueId(){
		return intent.getIntExtra(ISSUE_ID, -1);
	}
}
