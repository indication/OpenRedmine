package jp.redmine.redmineclient.activity.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import jp.redmine.redmineclient.IssueViewActivity;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import jp.redmine.redmineclient.param.IssueArgument;

public class ActionActivityHelper implements IntentAction {
	protected Context context;
	public ActionActivityHelper(Context act){
		context = act;
	}
	@Override
	public void issue(int connection, int issueid) {
		IssueArgument intent = new IssueArgument();
		intent.setIntent(context, IssueViewActivity.class);
		intent.setConnectionId(connection);
		intent.setIssueId(issueid);
		context.startActivity(intent.getIntent());
	}
	@Override
	public boolean url(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(intent);
		return true;
	}

}
