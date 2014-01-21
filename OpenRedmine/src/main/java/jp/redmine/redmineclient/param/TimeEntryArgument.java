package jp.redmine.redmineclient.param;

public class TimeEntryArgument extends IssueArgument {
	public static final String TIMEENTRY_ID = "TIMEENTRY";

	public void setTimeEntryId(int id){
		setArg(TIMEENTRY_ID,id);
	}
	public int getTimeEntryId(){
		return getArg(TIMEENTRY_ID, -1);
	}

	@Override
	public void importArgument(Core arg) {
		setTimeEntryId(((TimeEntryArgument)arg).getTimeEntryId());
		super.importArgument(arg);
	}
}
