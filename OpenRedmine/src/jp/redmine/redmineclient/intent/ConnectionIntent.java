package jp.redmine.redmineclient.intent;

import android.content.Context;
import android.content.Intent;

public class ConnectionIntent extends CoreIntent {
	public ConnectionIntent(Intent intent) {
		super(intent);
	}
	public ConnectionIntent(Context applicationContext,Class<?> cls) {
		super(applicationContext,cls);
	}
	public static final String CONNECTION_ID = "CONNECTIONID";

	public void setConnectionId(int id){
		intent.putExtra(CONNECTION_ID,id);
	}
	public int getConnectionId(){
		return intent.getIntExtra(CONNECTION_ID, -1);
	}
}
