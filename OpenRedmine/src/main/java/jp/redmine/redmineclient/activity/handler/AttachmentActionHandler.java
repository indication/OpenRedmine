package jp.redmine.redmineclient.activity.handler;

import android.content.Intent;

import jp.redmine.redmineclient.activity.AttachmentActivity;
import jp.redmine.redmineclient.param.AttachmentArgument;

public class AttachmentActionHandler extends Core implements AttachmentActionInterface {

	public AttachmentActionHandler(ActivityRegistry manager) {
		super(manager);
	}

	@Override
	public void onAttachmentSelected(final int connectionid, final int attachmentid) {
		kickActivity(AttachmentActivity.class, new IntentFactory() {
			@Override
			public void generateIntent(Intent intent) {
				AttachmentArgument arg = new AttachmentArgument();
				arg.setIntent(intent);
				arg.setConnectionId(connectionid);
				arg.setAttachmentId(attachmentid);
			}
		});
	}

}