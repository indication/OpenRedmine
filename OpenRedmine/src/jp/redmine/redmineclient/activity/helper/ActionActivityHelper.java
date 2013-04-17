package jp.redmine.redmineclient.activity.helper;

import android.content.Context;
import jp.redmine.redmineclient.IssueViewActivity;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import jp.redmine.redmineclient.intent.IssueIntent;

public class ActionActivityHelper implements IntentAction {
	protected Context context;
	public ActionActivityHelper(Context act){
		context = act;
	}
	@Override
	public void issue(int connection, int issueid) {
		IssueIntent intent = new IssueIntent(context, IssueViewActivity.class);
		intent.setConnectionId(connection);
		intent.setIssueId(issueid);
		context.startActivity(intent.getIntent());
	}

}
