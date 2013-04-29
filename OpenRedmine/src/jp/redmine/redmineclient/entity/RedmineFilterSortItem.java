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
	static public final String KEY_TRACKER = "tracker";
	static public final String KEY_STATUS = "status";
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
		items.add(KEY_TRACKER);
		items.add(KEY_STATUS);
		return items;
	}
	public static RedmineFilterSortItem setFilter(RedmineFilterSortItem item, String input){
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
		} else if(KEY_TRACKER.equals(keys[0])){
			item.setDbKey("RedmineTracker."+RedmineTracker.NAME);
			item.setRemoteKey(KEY_TRACKER);
			item.setResource(R.string.ticket_tracker);
		} else if(KEY_STATUS.equals(keys[0])){
			item.setDbKey("RedmineStatus."+RedmineStatus.NAME);
			item.setRemoteKey(KEY_STATUS);
			item.setResource(R.string.ticket_status);
		}

		item.setAscending(isAscending);
		return item;

	}
	public static List<RedmineFilterSortItem> getFilters(boolean isAddDesc){
		List<RedmineFilterSortItem> list = new ArrayList<RedmineFilterSortItem>();
		for(String key : getKeys()){
			RedmineFilterSortItem item;
			if(isAddDesc){
				item = setFilter(new RedmineFilterSortItem(),key);
				item.setAscending(false);
				list.add(item);
			}
			item = setFilter(new RedmineFilterSortItem(),key);
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


}
