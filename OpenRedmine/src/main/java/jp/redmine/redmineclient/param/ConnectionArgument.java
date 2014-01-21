package jp.redmine.redmineclient.param;

public class ConnectionArgument extends Core {
	public static final String CONNECTION_ID = "CONNECTIONID";

	public void setConnectionId(int id){
		setArg(CONNECTION_ID,id);
	}
	public int getConnectionId(){
		return getArg(CONNECTION_ID, -1);
	}

	@Override
	public void importArgument(Core arg) {
		setConnectionId(((ConnectionArgument)arg).getConnectionId());
	}
}
