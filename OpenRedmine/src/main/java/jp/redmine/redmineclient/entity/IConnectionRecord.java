package jp.redmine.redmineclient.entity;

public interface IConnectionRecord {
	public void setRedmineConnection(RedmineConnection connection);
	public void setConnectionId(Integer connection_id);
	public Integer getConnectionId();
}
