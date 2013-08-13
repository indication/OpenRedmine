package jp.redmine.redmineclient.entity;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import jp.redmine.redmineclient.R;

public class RedmineFilterSortItem implements IMasterRecord {
	private String dbKey;
	private String remoteKey;
	private boolean isAscending;
	private int resource;
	private String localname;
	static public final String KEY_ISSUE = "id";
	static public final String KEY_MODIFIED = "updated_on";
	static public final String KEY_CREATED = "created_on";
	static public final String KEY_DATE_START = "start_date";
	static public final String KEY_DATE_DUE = "due_date";
	static public final String KEY_DATE_CLOSED = "closed";
	static public final String KEY_PRIORITY = "priority";
	static public final String KEY_STATUS = "status";
	static public final String KEY_TRACKER = "tracker";
	static public final String KEY_VERSION = "fixed_version";
	static public final String KEY_CATEGORY = "category";
	static public final String KEY_ASSIGN = "assigned_to";
	static public final String KEY_AUTHOR = "author_id";
	static public final String KEY_DONERATE = "done_ratio";
	static public final String KEY_DEFAULT = "id desc";
	/**
	 * @param id セットする id
	 */
	public void setId(Long id) {
	}
	/**
	 * @return dbKey
	 */
	public String getDbKey() {
		return dbKey;
	}
	/**
	 * @param dbKey セットする dbKey
	 */
	public void setDbKey(String dbKey) {
		this.dbKey = dbKey;
	}
	/**
	 * @return remoteKey
	 */
	public String getRemoteKey() {
		return remoteKey;
	}
	/**
	 * @param remoteKey セットする remoteKey
	 */
	public void setRemoteKey(String remoteKey) {
		this.remoteKey = remoteKey;
	}
	/**
	 * @return isAscending
	 */
	public boolean isAscending() {
		return isAscending;
	}
	/**
	 * @param isAscending セットする isAscending
	 */
	public void setAscending(boolean isAscending) {
		this.isAscending = isAscending;
	}
	/**
	 * @return resource
	 */
	public int getResource() {
		return resource;
	}
	/**
	 * @param resource セットする resource
	 */
	public void setResource(int resource) {
		this.resource = resource;
	}
	@Override
	public void setRemoteId(Long id) {
		setId(id);
	}
	@Override
	public Long getRemoteId() {
		return getId();
	}
	@Override
	public void setName(String name) {
		localname = name;
	}
	@Override
	public String getName() {
		return localname;
	}
	/**
	 * @return id
	 */
	public Long getId() {
		long basekey = getKeys().indexOf(getRemoteKey());
		if(basekey == -1)
			return basekey;
		basekey *= 2;
		basekey += isAscending() ? 0 : 1;
		return basekey;
	}
	public static List<String> getKeys(){
		List<String> items = new ArrayList<String>();
		items.add(KEY_ISSUE);
		items.add(KEY_MODIFIED);
		items.add(KEY_CREATED);
		items.add(KEY_DATE_START);
		items.add(KEY_DATE_DUE);
		items.add(KEY_PRIORITY);
		items.add(KEY_STATUS);
		items.add(KEY_TRACKER);
		items.add(KEY_VERSION);
		items.add(KEY_CATEGORY);
		items.add(KEY_ASSIGN);
		items.add(KEY_AUTHOR);
		items.add(KEY_DONERATE);
		items.add(KEY_DATE_CLOSED);
		return items;
	}
	public static RedmineFilterSortItem setupFilter(RedmineFilterSortItem item, String input){
		if(TextUtils.isEmpty(input))
			input = KEY_DEFAULT;
		String[] keys = input.split(" ");
		boolean isAscending = false;
		if(keys.length >= 2){
			if("desc".equalsIgnoreCase(keys[1]))
				isAscending = false;
			else
				isAscending = true;
		} else {
			isAscending = true;
		}
		if(KEY_ISSUE.equals(keys[0])){
			item.setDbKey(RedmineIssue.ISSUE_ID);
			item.setRemoteKey(KEY_ISSUE);
			item.setResource(R.string.ticket_issue);
		} else if(KEY_MODIFIED.equals(keys[0])){
			item.setDbKey(RedmineIssue.MODIFIED);
			item.setRemoteKey(KEY_MODIFIED);
			item.setResource(R.string.ticket_modified);
		} else if(KEY_CREATED.equals(keys[0])){
			item.setDbKey(RedmineIssue.CREATED);
			item.setRemoteKey(KEY_CREATED);
			item.setResource(R.string.ticket_created);
		} else if(KEY_DATE_START.equals(keys[0])){
			item.setDbKey(RedmineIssue.DATE_START);
			item.setRemoteKey(KEY_DATE_START);
			item.setResource(R.string.ticket_date_start);
		} else if(KEY_DATE_DUE.equals(keys[0])){
			item.setDbKey(RedmineIssue.DATE_DUE);
			item.setRemoteKey(KEY_DATE_DUE);
			item.setResource(R.string.ticket_date_due);
		} else if(KEY_DATE_CLOSED.equals(keys[0])){
			item.setDbKey(RedmineIssue.DATE_CLOSED);
			item.setRemoteKey(KEY_DATE_CLOSED);
			item.setResource(R.string.ticket_date_closed);
		} else if(KEY_PRIORITY.equals(keys[0])){
			item.setDbKey(RedmineIssue.PRIORITY);
			item.setRemoteKey(KEY_PRIORITY);
			item.setResource(R.string.ticket_priority);
		} else if(KEY_STATUS.equals(keys[0])){
			item.setDbKey(RedmineIssue.STATUS);
			item.setRemoteKey(KEY_STATUS);
			item.setResource(R.string.ticket_status);
		} else if(KEY_TRACKER.equals(keys[0])){
			item.setDbKey(RedmineIssue.TRACKER);
			item.setRemoteKey(KEY_TRACKER);
			item.setResource(R.string.ticket_tracker);
		} else if(KEY_VERSION.equals(keys[0])){
			item.setDbKey(RedmineIssue.VERSION);
			item.setRemoteKey(KEY_VERSION);
			item.setResource(R.string.ticket_version);
		} else if(KEY_CATEGORY.equals(keys[0])){
			item.setDbKey(RedmineIssue.CATEGORY);
			item.setRemoteKey(KEY_CATEGORY);
			item.setResource(R.string.ticket_category);
		} else if(KEY_ASSIGN.equals(keys[0])){
			item.setDbKey(RedmineIssue.ASSIGN);
			item.setRemoteKey(KEY_ASSIGN);
			item.setResource(R.string.ticket_assigned);
		} else if(KEY_AUTHOR.equals(keys[0])){
			item.setDbKey(RedmineIssue.AUTHOR);
			item.setRemoteKey(KEY_AUTHOR);
			item.setResource(R.string.ticket_author);
		} else if(KEY_DONERATE.equals(keys[0])){
			item.setDbKey(RedmineIssue.PROGRESS);
			item.setRemoteKey(KEY_DONERATE);
			item.setResource(R.string.ticket_progress);
		}

		item.setAscending(isAscending);
		return item;

	}
	public static List<RedmineFilterSortItem> getFilters(boolean isAddDesc){
		List<RedmineFilterSortItem> list = new ArrayList<RedmineFilterSortItem>();
		for(String key : getKeys()){
			RedmineFilterSortItem item;
			if(isAddDesc){
				item = setupFilter(new RedmineFilterSortItem(),key);
				item.setAscending(false);
				list.add(item);
			}
			item = setupFilter(new RedmineFilterSortItem(),key);
			item.setAscending(true);
			list.add(item);
		}
		return list;
	}
	public static String getFilter(List<RedmineFilterSortItem> items){
		StringBuilder sb = new StringBuilder();
		for(RedmineFilterSortItem item : items){
			if(sb.length() > 0 )
				sb.append(",");
			sb.append(getFilter(item));
		}
		String result = sb.toString();
		return result.equals(KEY_DEFAULT) ? "" : result;
	}
	public static String getFilter(RedmineFilterSortItem item){
		return item.isAscending() ? item.getRemoteKey() : item.getRemoteKey()+" desc";
	}
	public static String getFilter(String key, boolean isAscending){
		RedmineFilterSortItem item = setupFilter(new RedmineFilterSortItem(),key);
		item.setAscending(isAscending);
		return getFilter(item);
	}


}
