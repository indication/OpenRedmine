package jp.redmine.redmineclient.entity;

import java.io.Serializable;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RedmineJournalChanges<T extends IMasterRecord>
	implements Serializable
	{
	/**
	 * Unique object id
	 */
	private static final long serialVersionUID = 2477102604577302259L;

	private String name;
	private T before;
	private T after;
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
	public void setBefore(T before) {
		this.before = before;
	}
	/**
	 * @return before
	 */
	public T getBefore() {
		return before;
	}
	/**
	 * @param after セットする after
	 */
	public void setAfter(T after) {
		this.after = after;
	}
	/**
	 * @return after
	 */
	public T getAfter() {
		return after;
	}

}
