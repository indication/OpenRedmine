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

	private transient IMasterRecord masterBefore;
	private transient IMasterRecord masterAfter;
	private transient Integer resourceId;
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
		if(getMasterBefore() == null || TextUtils.isEmpty(getMasterBefore().getName()))
			return "";
		return getMasterBefore().getName();
	}

	public String getMasterNameAfter(){
		if(getMasterAfter() == null || TextUtils.isEmpty(getMasterAfter().getName()))
			return "";
		return getMasterAfter().getName();
	}
	/**
	 * @return resourceId
	 */
	public Integer getResourceId() {
		return resourceId;
	}
	/**
	 * @param resourceId セットする resourceId
	 */
	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}
	/**
	 * @return masterBefore
	 */
	public IMasterRecord getMasterBefore() {
		return masterBefore;
	}
	/**
	 * @param masterBefore セットする masterBefore
	 */
	public void setMasterBefore(IMasterRecord masterBefore) {
		this.masterBefore = masterBefore;
	}
	/**
	 * @return masterAfter
	 */
	public IMasterRecord getMasterAfter() {
		return masterAfter;
	}
	/**
	 * @param masterAfter セットする masterAfter
	 */
	public void setMasterAfter(IMasterRecord masterAfter) {
		this.masterAfter = masterAfter;
	}
}
