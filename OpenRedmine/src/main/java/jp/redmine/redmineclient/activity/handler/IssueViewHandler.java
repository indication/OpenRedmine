package jp.redmine.redmineclient.activity.handler;

import android.content.Intent;
import android.net.Uri;

import jp.redmine.redmineclient.activity.IssueActivity;
import jp.redmine.redmineclient.activity.IssueFilterActivity;
import jp.redmine.redmineclient.activity.KanbanActivity;
import jp.redmine.redmineclient.activity.ProjectActivity;
import jp.redmine.redmineclient.activity.WebViewActivity;
import jp.redmine.redmineclient.activity.WikiViewActivity;
import jp.redmine.redmineclient.param.FilterArgument;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.param.ProjectArgument;
import jp.redmine.redmineclient.param.WebArgument;
import jp.redmine.redmineclient.param.WikiArgument;

public class IssueViewHandler extends Core
	implements IssueActionInterface, WebviewActionInterface {

	public IssueViewHandler(ActivityRegistry manager) {
		super(manager);
	}

	@Override
	public void onIssueFilterList(final int connectionid, final int filterid) {
		kickActivity(IssueFilterActivity.class, new IntentFactory() {
			@Override
			public void generateIntent(Intent intent) {
				FilterArgument arg = new FilterArgument();
				arg.setIntent(intent);
				arg.setConnectionId(connectionid);
				arg.setFilterId(filterid);
			}
		});
	}

	@Override
	public void onIssueList(final int connectionid, final long projectid) {
		kickActivity(ProjectActivity.class, new IntentFactory() {
			@Override
			public void generateIntent(Intent intent) {
				ProjectArgument arg = new ProjectArgument();
				arg.setIntent(intent);
				arg.setConnectionId(connectionid);
				arg.setProjectId(projectid);
			}
		});
	}

	@Override
	public void onKanbanList(final int connectionid, final long projectid) {
		kickActivity(KanbanActivity.class, new IntentFactory() {
			@Override
			public void generateIntent(Intent intent) {
				ProjectArgument arg = new ProjectArgument();
				arg.setIntent(intent);
				arg.setConnectionId(connectionid);
				arg.setProjectId(projectid);
			}
		});
	}

	@Override
	public void onIssueSelected(final int connectionid, final int issueid) {
		kickActivity(IssueActivity.class, new IntentFactory() {
			@Override
			public void generateIntent(Intent intent) {
				IssueArgument arg = new IssueArgument();
				arg.setIntent(intent);
				arg.setConnectionId(connectionid);
				arg.setIssueId(issueid);
			}
		});
	}

	@Override
	public void onIssueEdit(final int connectionid, final int issueid) {
        kickActivity(IssueActivity.class, new IntentFactory() {
            @Override
            public void generateIntent(Intent intent) {
                IssueArgument arg = new IssueArgument();
                arg.setIntent(intent);
                arg.setConnectionId(connectionid);
                arg.setIssueId(issueid);
                arg.setIsEdit(true);

            }
        });
	}

	@Override
	public void onIssueAdd(final int connectionid, final long projectId) {
		kickActivity(IssueActivity.class, new IntentFactory() {
			@Override
			public void generateIntent(Intent intent) {
				IssueArgument arg = new IssueArgument();
				arg.setIntent(intent);
				arg.setConnectionId(connectionid);
				arg.setProjectId(projectId);
				arg.setIssueId(-1);

			}
		});
	}

	@Override
	public void onIssueRefreshed(int connectionid, int issueid) {
		//TODO

	}

	@Override
	public void issue(int connection, int issueid) {
		onIssueSelected(connection, issueid);
	}

	@Override
	public boolean url(final String url, final Integer connection) {
		if (connection == null) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			manager.kickActivity(intent);
		} else {
			kickActivity(WebViewActivity.class, new IntentFactory() {
				@Override
				public void generateIntent(Intent intent) {
					WebArgument arg = new WebArgument();
					arg.setIntent(intent);
					arg.setConnectionId(connection);
					arg.setUrl(url);
				}
			});
		}
		return true;
	}

	@Override
	public void wiki(final int connection, final long projectid, final String title) {
		kickActivity(WikiViewActivity.class, new IntentFactory() {
			@Override
			public void generateIntent(Intent intent) {
				WikiArgument arg = new WikiArgument();
				arg.setIntent(intent);
				arg.setConnectionId(connection);
				arg.setProjectId(projectid);
				arg.setWikiTitle(title);
			}
		});
	}

}
