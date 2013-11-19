package jp.redmine.redmineclient.param;

public class IssueArgument extends ProjectArgument {
	public static final String ISSUE_ID = "ISSUEID";

	public void setIssueId(int id){
		setArg(ISSUE_ID,id);
	}
	public int getIssueId(){
		return getArg(ISSUE_ID, -1);
	}
}
