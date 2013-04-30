package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;
import java.util.HashMap;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineJournalModel;
import jp.redmine.redmineclient.db.cache.RedmineStatusModel;
import jp.redmine.redmineclient.db.cache.RedmineUserModel;
import jp.redmine.redmineclient.db.cache.RedmineVersionModel;
import jp.redmine.redmineclient.entity.DummySelection;
import jp.redmine.redmineclient.entity.IMasterRecord;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedmineJournalChanges;
import jp.redmine.redmineclient.entity.TypeConverter;
import jp.redmine.redmineclient.form.RedmineJournalListItemForm;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class RedmineJournalListAdapter extends RedmineBaseAdapter<RedmineJournal> {
	private final static String TAG = "RedmineJournalListAdapter";

	private RedmineJournalModel mJournal;
	private RedmineVersionModel mVersion;
	private RedmineUserModel mUser;
	private RedmineStatusModel mStatus;
	protected Integer connection_id;
	protected Long issue_id;
	protected IntentAction action;
	protected HashMap<String,fetchHelper> fetchMap = new HashMap<String, RedmineJournalListAdapter.fetchHelper>();

	protected void setupHashmap(){
		fetchMap.put("done_ratio", new fetchHelper(){
			@Override
			protected IMasterRecord getRawItem(String input) {
				DummySelection item = new DummySelection();
				item.setName(input + "%");
				return item;
			}
			@Override
			public int getResourceNameId() {
				return R.string.ticket_progress;
			}
		});
		fetchMap.put("due_date", new fetchHelper(){
			@Override
			protected IMasterRecord getRawItem(String input) {
				DummySelection item = new DummySelection();
				item.setName(input);
				return item;
			}
			@Override
			public int getResourceNameId() {
				return R.string.ticket_date_due;
			}
		});
		fetchMap.put("start_date", new fetchHelper(){
			@Override
			protected IMasterRecord getRawItem(String input) {
				DummySelection item = new DummySelection();
				item.setName(input);
				return item;
			}
			@Override
			public int getResourceNameId() {
				return R.string.ticket_date_start;
			}
		});
		fetchMap.put("fixed_version", new fetchHelper(){
			@Override
			protected IMasterRecord getRawItem(String input) throws SQLException {
				if(connection_id == null)
					return null;
				return mVersion.fetchById(connection_id, TypeConverter.parseInteger(input));
			}
			@Override
			public int getResourceNameId() {
				return R.string.ticket_version;
			}
		});
		fetchMap.put("assigned_to_id", new fetchHelper(){
			@Override
			protected IMasterRecord getRawItem(String input) throws SQLException {
				if(connection_id == null)
					return null;
				return mUser.fetchById(connection_id, TypeConverter.parseInteger(input));
			}
			@Override
			public int getResourceNameId() {
				return R.string.ticket_assigned;
			}
		});
		fetchMap.put("status_id", new fetchHelper(){
			@Override
			protected IMasterRecord getRawItem(String input) throws SQLException {
				if(connection_id == null)
					return null;
				return mStatus.fetchById(connection_id, TypeConverter.parseInteger(input));
			}
			@Override
			public int getResourceNameId() {
				return R.string.ticket_status;
			}
		});
	}

	private abstract class fetchHelper{
		abstract protected IMasterRecord getRawItem(String input) throws SQLException;
		abstract public int getResourceNameId();
		public IMasterRecord getItem(String input) throws SQLException{
			if(TextUtils.isEmpty(input))
				return null;
			return getRawItem(input);
		}
	}


	public RedmineJournalListAdapter(DatabaseCacheHelper m,IntentAction act){
		super();
		mJournal = new RedmineJournalModel(m);
		mVersion = new RedmineVersionModel(m);
		mUser = new RedmineUserModel(m);
		mStatus = new RedmineStatusModel(m);
		action = act;
		setupHashmap();
	}

	public void setupParameter(int connection, long issue){
		connection_id = connection;
		issue_id = issue;
	}

	public boolean isValidParameter(){
		if(issue_id == null || connection_id == null)
			return false;
		else
			return true;
	}

	@Override
	protected View getItemView(LayoutInflater infalInflater) {
		return  infalInflater.inflate(R.layout.journalitem, null);
	}

	@Override
	protected void setupView(View view, RedmineJournal data) {
		RedmineJournalListItemForm form = new RedmineJournalListItemForm(view);
		form.setupWebView(action);
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		if(!isValidParameter())
			return 0;
		return (int) mJournal.countByIssue(connection_id, issue_id);
	}

	@Override
	protected RedmineJournal getDbItem(int position) throws SQLException {
		if(!isValidParameter())
			return null;
		RedmineJournal jr = mJournal.fetchItemByIssue(connection_id, issue_id,(long) position, 1);
		for(RedmineJournalChanges cg : jr.changes){
			if("attr".equalsIgnoreCase(cg.getProperty()))
				getAttributeDetail(cg);
			else
				Log.w(TAG,"Changes: " + cg.getName() + "," + cg.getProperty());
		}
		return jr;
	}

	protected void getAttributeDetail(RedmineJournalChanges cg) throws SQLException{
		if(TextUtils.isEmpty(cg.getName()))
			return;
		String name = cg.getName();
		if(!fetchMap.containsKey(name)){
			Log.w(TAG,"Undefined key: " + name + "," + cg.getProperty());
			return;
		}
		fetchHelper helper = fetchMap.get(name);
		cg.setResourceId(helper.getResourceNameId());
		cg.setMasterBefore(helper.getItem(cg.getBefore()));
		cg.setMasterAfter(helper.getItem(cg.getAfter()));
	}
	@Override
	protected long getDbItemId(RedmineJournal item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}


}
