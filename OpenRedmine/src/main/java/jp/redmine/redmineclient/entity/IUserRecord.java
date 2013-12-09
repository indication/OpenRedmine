package jp.redmine.redmineclient.entity;

public interface IUserRecord {

	public Integer getConnectionId();
	public RedmineUser getUser();
	public void setUser(RedmineUser data);
}
