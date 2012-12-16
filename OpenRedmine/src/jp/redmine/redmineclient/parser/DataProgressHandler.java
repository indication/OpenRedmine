package jp.redmine.redmineclient.parser;

public interface DataProgressHandler{
	public void onGetOffset(int offset);
	public void onGetLimit(int limit);
	public void onGetTotal(int total);
	public void onProgress(int progress);
}