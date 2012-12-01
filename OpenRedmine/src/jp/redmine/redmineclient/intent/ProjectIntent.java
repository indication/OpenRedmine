package jp.redmine.redmineclient.intent;

import android.content.Context;
import android.content.Intent;

public class ProjectIntent extends ConnectionIntent {
	public ProjectIntent(Intent intent) {
		super(intent);
	}
	public ProjectIntent(Context applicationContext,Class<?> cls) {
		super(applicationContext,cls);
	}
	public static final String PROJECT_ID = "PROJECTID";

	public void setProjectId(long id){
		intent.putExtra(PROJECT_ID,id);
	}
	public long getProjectId(){
		return intent.getLongExtra(PROJECT_ID, -1);
	}
}
