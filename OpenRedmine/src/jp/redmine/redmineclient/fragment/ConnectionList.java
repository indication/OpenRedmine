package jp.redmine.redmineclient.fragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.ConnectionListAdapter;
import jp.redmine.redmineclient.db.store.DatabaseHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import android.app.Activity;
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
	private OnArticleSelectedListener mListener;
	public interface OnArticleSelectedListener {
		public void onArticleSelected(int connectionid);
		public void onArticleEditSelected(int connectionid);
		public void onArticleAddSelected();
	}

	public ConnectionList(){
		super();
	}

	static public ConnectionList newInstance(){
		return new ConnectionList();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnArticleSelectedListener){
			mListener = (OnArticleSelectedListener)activity;
		} else {
			//setup empty events
			mListener = new OnArticleSelectedListener() {

				@Override
				public void onArticleSelected(int connectionid) {

				}

				@Override
				public void onArticleEditSelected(int connectionid) {

				}

				@Override
				public void onArticleAddSelected() {

				}
			};
		}

	}
	@Override
	public void onDestroy() {
		mListener = null;
		super.onDestroy();
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
				mListener.onArticleEditSelected(item.getId());
				return true;
			}
		});

		mFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onArticleAddSelected();
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
		mListener.onArticleSelected(item.getId());
	}



}
