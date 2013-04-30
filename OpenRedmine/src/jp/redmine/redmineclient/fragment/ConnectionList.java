package jp.redmine.redmineclient.fragment;

import jp.redmine.redmineclient.ProjectListActivity;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.intent.ConnectionIntent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ConnectionList extends ListFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//TODO
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		super.onListItemClick(parent, v, position, id);
		ListView listView = (ListView) parent;
		RedmineConnection item = (RedmineConnection) listView.getItemAtPosition(position);
		onItemSelect(item.getId());
	}
	protected void onItemSelect(int id){
		ConnectionIntent intent = new ConnectionIntent( getActivity().getApplicationContext(), ProjectListActivity.class );
		intent.setConnectionId(id);
		startActivity( intent.getIntent() );
	}
}
