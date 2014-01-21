package jp.redmine.redmineclient.param;

public class ProjectArgument extends ConnectionArgument {
	public static final String PROJECT_ID = "PROJECTID";

	public void setProjectId(long id){
		setArg(PROJECT_ID,id);
	}
	public long getProjectId(){
		return getArg(PROJECT_ID, -1L);
	}

	@Override
	public void importArgument(Core arg) {
		setProjectId(((ProjectArgument)arg).getProjectId());
		super.importArgument(arg);
	}
}
