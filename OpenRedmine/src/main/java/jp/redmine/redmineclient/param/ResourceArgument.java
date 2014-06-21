package jp.redmine.redmineclient.param;

public class ResourceArgument extends Core {
	public static final String RESOURCE = "RECOURCE";

	public void setResource(Integer id){
		setArg(RESOURCE,id);
	}
	public Integer getResource(){
		return getArg(RESOURCE,-1);
	}

	public void importArgument(ResourceArgument arg) {
		setResource(arg.getResource());
		super.importArgument(arg);
	}
}
