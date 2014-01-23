package jp.redmine.redmineclient.param;

public class AttachmentArgument extends IssueArgument {
	public static final String ATTACHMENT_ID = "ATTACHMENT";

	public void setAttachmentId(int id){
		setArg(ATTACHMENT_ID,id);
	}
	public int getAttachmentId(){
		return getArg(ATTACHMENT_ID, -1);
	}

	public void importArgument(AttachmentArgument arg) {
		setAttachmentId(arg.getAttachmentId());
		super.importArgument(arg);
	}
}
