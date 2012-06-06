package jp.redmine.redmineclient.external;

public interface DataCreationHandler<CON,TYPE>{
	public void onData(CON info,TYPE data);
}