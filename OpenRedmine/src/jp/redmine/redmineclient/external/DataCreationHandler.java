package jp.redmine.redmineclient.external;

public interface DataCreationHandler<TYPE>{
	public void onData(TYPE data);
}