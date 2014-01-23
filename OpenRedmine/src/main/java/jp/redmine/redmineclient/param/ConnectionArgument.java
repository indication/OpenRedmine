package jp.redmine.redmineclient.param;

public class ConnectionArgument extends Core {
	public static final String CONNECTION_ID = "CONNECTIONID";

	public void setConnectionId(int id){
		setArg(CONNECTION_ID,id);
	}
	public int getConnectionId(){
		return getArg(CONNECTION_ID, -1);
	}

	public void importArgument(ConnectionArgument arg) {
		setConnectionId(arg.getConnectionId());
		super.importArgument(arg);
	}
}
