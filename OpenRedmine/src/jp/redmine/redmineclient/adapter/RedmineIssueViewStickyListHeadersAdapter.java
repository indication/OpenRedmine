package jp.redmine.redmineclient.adapter;

import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;

/**
 * ListAdapter junction
 * Combine the list of issue and journals and relations
 * 
 * Example:
 * 	Detail
 * 		IssueDetailView
 * 	Relations
 * 		RelationsView
 * 	Journals
 * 		JournalListView
 */
public class RedmineIssueViewStickyListHeadersAdapter extends BaseAdapter implements StickyListHeadersAdapter {
	@SuppressWarnings("unused")
	private final static String TAG = "RedmineIssueViewStickyListHeadersAdapter";

	private RedmineJournalListAdapter adapterJournal;
	private RedmineIssueDetailAdapter adapterIssue;
	private final List<AggrigateAdapter> mapAdapters = new ArrayList<AggrigateAdapter>();
	
	public RedmineIssueViewStickyListHeadersAdapter(DatabaseCacheHelper m,IntentAction act){
		adapterJournal = new RedmineJournalListAdapter(m, act);
		adapterIssue = new RedmineIssueDetailAdapter(m, act);
		mapAdapters.add(new AggrigateAdapter(adapterIssue, R.string.ticket_detail));
		mapAdapters.add(new AggrigateAdapter(adapterJournal, R.string.ticket_journals));
	}

	protected class AggrigateAdapter {
		public final BaseAdapter adapter;
		public int count;
		public int head;
		public final int res;
		public AggrigateAdapter(BaseAdapter a, int r){
			adapter = a;
			res = r;
		}
		public int getInnerPos(int pos){
			return pos - head;
		}
		public Object getItem(int pos){
			return adapter == null ? null : adapter.getItem(getInnerPos(pos));
		}
		public long getItemId(int pos){
			return adapter == null ? 0 : adapter.getItemId(getInnerPos(pos));
		}
		public View getView(int pos, View convertView, ViewGroup parent) {
			return adapter == null ? null : adapter.getView(getInnerPos(pos), convertView, parent);
		}
		public View getDropDownView(int pos, View convertView, ViewGroup parent) {
			return adapter == null ? null : adapter.getDropDownView(getInnerPos(pos), convertView, parent);
		}
		public int getItemViewType(int pos) {
			return adapter == null ? 0 : adapter.getItemViewType(getInnerPos(pos));
		}
	}
	
	@Override
	public void notifyDataSetChanged() {

		int current = 0;
		for(AggrigateAdapter adapter : mapAdapters){
			adapter.adapter.notifyDataSetChanged();
			adapter.head = current;
			adapter.count = adapter.adapter.getCount();
			current += adapter.count;
		}
		super.notifyDataSetChanged();
	}
	protected AggrigateAdapter getInner(int pos){
		AggrigateAdapter lastadapter = null;
		for(AggrigateAdapter item : mapAdapters){
			if(item.head <= pos){
				lastadapter = item;
			}
		}
		//returns default
		return lastadapter;
	}
	public void setupParameter(int connection, long issue){
		adapterIssue.setupParameter(connection, issue);
		adapterJournal.setupParameter(connection, issue);
	}

	@Override
	public void notifyDataSetInvalidated() {
		if(mapAdapters != null){
			for(AggrigateAdapter adapter : mapAdapters){
				adapter.adapter.notifyDataSetInvalidated();
			}
		}
		super.notifyDataSetInvalidated();
	}
	
	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		AggrigateAdapter adapter = getInner(position);
		if(adapter.adapter instanceof StickyListHeadersAdapter){
			return ((StickyListHeadersAdapter) adapter.adapter).getHeaderView(adapter.getInnerPos(position), convertView, parent);
		} else {
			LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.issuestickyheader, null);
			if (convertView != null){
				TextView text = (TextView) convertView.findViewById(R.id.textTitle);
				text.setText(adapter == null ? "" : convertView.getContext().getString(getInner(position).res));
			}
		}
		return convertView;
	}

	@Override
	public long getHeaderId(int pos) {
		AggrigateAdapter adapter = getInner(pos);
		if(adapter.adapter instanceof StickyListHeadersAdapter){
			// To take a uniq id by plus resource id for the adapter
			// Be careful with using ids on child adapter that use the lower bits of the id.
			return	 ((long)((StickyListHeadersAdapter) adapter.adapter).getHeaderId(adapter.getInnerPos(pos))) | (((long)adapter.res) << Integer.SIZE) ;
		}
		return adapter == null ? 0 : adapter.res;
	}

	@Override
	public int getCount() {
		AggrigateAdapter lastadapter = null;
		for(AggrigateAdapter item : mapAdapters){
			lastadapter = item;
		}
		return lastadapter == null ? 0 : lastadapter.head + lastadapter.count;
	}
	
	public int getJournalCount(){
		return adapterJournal.getCount();
	}

	@Override
	public int getItemViewType(int pos) {
		AggrigateAdapter adapter = getInner(pos);
		return adapter == null ? android.R.layout.simple_list_item_1 : adapter.getItemViewType(pos);
	}
	

	@Override
	public Object getItem(int pos) {
		return getInner(pos).getItem(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		AggrigateAdapter adapter = getInner(pos);
		convertView = adapter == null ? null : adapter.getView(pos, convertView, parent);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(android.R.layout.simple_list_item_1, null);
		}
		return convertView;
	}
	
	@Override
	public View getDropDownView(int pos, View convertView, ViewGroup parent) {
		AggrigateAdapter adapter = getInner(pos);
		convertView = adapter == null ? null : adapter.getDropDownView(pos, convertView, parent);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
		}
		return convertView;
	}

}
