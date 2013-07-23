package jp.redmine.redmineclient.fragment;

public interface ActivityInterface {
	public <T> T getHandler(Class<T> cls);
}
