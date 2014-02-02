package jp.redmine.redmineclient.param;

public class IssueArgument extends ProjectArgument {
	public static final String ISSUE_ID = "ISSUEID";
	public static final String ISSUE_EDIT = "ISSUE_EDIT";

	public void setIssueId(int id){
		setArg(ISSUE_ID,id);
	}
	public int getIssueId(){
		return getArg(ISSUE_ID, -1);
	}

	public void setIsEdit(boolean edit){
		setArg(ISSUE_EDIT, edit);
	}

	public boolean isEdit(){
		return getArg(ISSUE_EDIT, false);
	}

	public void importArgument(IssueArgument arg) {
		setIssueId(arg.getIssueId());
		setIsEdit(arg.isEdit());
		super.importArgument(arg);
	}
}
