package jp.redmine.redmineclient.entity;

import java.io.Serializable;

import android.text.TextUtils;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RedmineJournalChanges
	implements Serializable
	{
	/**
	 * Unique object id
	 */
	private static final long serialVersionUID = 2477102604577302259L;

	private String property;
	private String name;
	private String before;
	private String after;

	public transient IMasterRecord masterBefore;
	public transient IMasterRecord masterAfter;
	public transient Integer resourceId;
	/**
	 * @return property
	 */
	public String getProperty() {
		return property;
	}
	/**
	 * @param property セットする property
	 */
	public void setProperty(String property) {
		this.property = property;
	}
	/**
	 * @param name セットする name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param before セットする before
	 */
	public void setBefore(String before) {
		this.before = before;
	}
	/**
	 * @return before
	 */
	public String getBefore() {
		return before;
	}
	/**
	 * @param after セットする after
	 */
	public void setAfter(String after) {
		this.after = after;
	}
	/**
	 * @return after
	 */
	public String getAfter() {
		return after;
	}

	public String getMasterNameBefore(){
		if(masterBefore == null || TextUtils.isEmpty(masterBefore.getName()))
			return "";
		return masterBefore.getName();
	}

	public String getMasterNameAfter(){
		if(masterAfter == null || TextUtils.isEmpty(masterAfter.getName()))
			return "";
		return masterAfter.getName();
	}
}
