package jp.redmine.redmineclient.entity;

import java.io.Serializable;
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

}
