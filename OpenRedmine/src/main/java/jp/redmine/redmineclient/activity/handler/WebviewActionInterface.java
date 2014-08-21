package jp.redmine.redmineclient.activity.handler;

public interface WebviewActionInterface{
	public void issue(int connection,int issueid);
	public boolean url(String url, Integer connection);
	public void wiki(int connection, long projectid, String title);
}