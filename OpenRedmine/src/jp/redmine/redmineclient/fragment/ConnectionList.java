package jp.redmine.redmineclient.fragment;

import jp.redmine.redmineclient.ConnectionActivity;
import jp.redmine.redmineclient.ProjectListActivity;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.ConnectionListAdapter;
import jp.redmine.redmineclient.db.store.DatabaseHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.intent.ConnectionIntent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class ConnectionList extends ListFragment {
	private DatabaseHelper helperStore;
	private ConnectionListAdapter adapter;
	private View mFooter;

	public ConnectionList(){
		super();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(helperStore != null){
			helperStore.close();
			helperStore = null;
		}
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().addFooterView(mFooter);
		getListView().setFastScrollEnabled(true);

		helperStore = new DatabaseHelper(getActivity());

		adapter = new ConnectionListAdapter(helperStore);
		setListAdapter(adapter);

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				ListView listView = (ListView) parent;
				RedmineConnection item = (RedmineConnection) listView.getItemAtPosition(position);
				ConnectionIntent intent = new ConnectionIntent( getActivity(), ConnectionActivity.class );
				intent.setConnectionId(item.getId());
				startActivity( intent.getIntent() );
				return false;
			}
		});

		mFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ConnectionIntent intent = new ConnectionIntent( getActivity().getApplicationContext(), ProjectListActivity.class );
				intent.setConnectionId(-1);
				startActivity( intent.getIntent() );
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFooter = inflater.inflate(R.layout.listview_add,container, false);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	@Override
	public void onStart() {
		super.onStart();
		if(adapter != null){
			adapter.notifyDataSetInvalidated();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		super.onListItemClick(parent, v, position, id);
		ListView listView = (ListView) parent;
		RedmineConnection item = (RedmineConnection) listView.getItemAtPosition(position);
		ConnectionIntent intent = new ConnectionIntent( getActivity().getApplicationContext(), ProjectListActivity.class );
		intent.setConnectionId(item.getId());
		startActivity( intent.getIntent() );
	}



}
