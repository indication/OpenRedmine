package jp.redmine.redmineclient.intent;

import android.content.Context;
import android.content.Intent;

public class CoreIntent {
	protected Intent intent;
	public CoreIntent(Intent intent){
		this.intent = intent;
	}
	public CoreIntent(Context context, Class<?> cls){
		this.intent = new Intent(context, cls);
	}
	public Intent getIntent(){
		return this.intent;
	}
}
