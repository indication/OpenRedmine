package jp.redmine.redmineclient.entity;

public class RedmineFilterSortItem {
	private String dbKey;
	private String remoteKey;
	private boolean isAscending;
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

	public static void setFilter(RedmineFilterSortItem item, String input){
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
		if("issue".equals(keys[0])){
			item.setDbKey(RedmineIssue.ISSUE_ID);
			item.setRemoteKey("id");
		} else if("tracker".equals(keys[0])){
			item.setDbKey("RedmineTracker."+RedmineTracker.NAME);
			item.setRemoteKey("tracker");
		} else if("status".equals(keys[0])){
			item.setDbKey("RedmineStatus."+RedmineStatus.NAME);
			item.setRemoteKey("status");
		}

		item.setAscending(isAscending);

	}


}
