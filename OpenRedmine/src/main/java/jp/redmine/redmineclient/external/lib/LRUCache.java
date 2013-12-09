package jp.redmine.redmineclient.external.lib;

import java.util.LinkedHashMap;
/**
 * LRU cache class
 * inherits from http://ttimez.blogspot.jp/2011/07/java.html
 * @author Takeshi Suzuki
 * @param <K> Key
 * @param <V> Value
 */
public class LRUCache<K,V> extends LinkedHashMap<K,V>{
	private static final long serialVersionUID = 1L;
	private int maxEntries;
	public LRUCache(int maxEntries) {
		super(maxEntries,(float) 0.75,true);
		this.maxEntries=maxEntries;
	}
	protected boolean removeEldestEntry(Entry<K,V> eldest) {
		return size()>maxEntries;
	}
	public interface IFetchObject<K>{
		public Object getItem(K key);
	}
	public V getValue(K key,IFetchObject<K> miss){
		if(containsKey(key)){
			return get(key);
		} else {
			@SuppressWarnings("unchecked")
			V val = (V)miss.getItem(key);
			put(key, val);
			return val;
		}
	}
}
