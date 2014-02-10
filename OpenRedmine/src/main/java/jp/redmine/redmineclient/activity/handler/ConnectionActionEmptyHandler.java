package jp.redmine.redmineclient.activity.handler;

public class ConnectionActionEmptyHandler implements ConnectionActionInterface {
	@Override
	public void onConnectionSelected(int connectionid) {}
	@Override
	public void onConnectionEdit(int connectionid) {}
	@Override
	public void onConnectionAdd() {}
	@Override
	public void onConnectionSaved() {}
}