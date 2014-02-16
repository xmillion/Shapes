package com.left.shap.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Least Recently Used Cache
 * implemented using a LinkedHashMap.
 * Source: http://steigert.blogspot.com.br/2012/03/8-libgdx-tutorial-sound-and-music.html
 * Reference: http://stackoverflow.com/questions/224868/easy-simple-to-use-lru-cache-in-java
 */
public class LRUCache<K, V> {
	
	public interface CacheEntryRemovedListener<K, V> {
		void notifyEntryRemoved(K key, V value);
	}
	
	private final int MAX_ENTRIES;
	private final Map<K, V> cache;
	private CacheEntryRemovedListener<K, V> removeListener;
	
	public LRUCache(int max) {
		MAX_ENTRIES = max;
		cache = new LinkedHashMap<K, V>(MAX_ENTRIES + 1, 0.75f, true) {
			private static final long serialVersionUID = 1L;
			public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				if(size() > MAX_ENTRIES) {
					if(removeListener != null) {
						removeListener.notifyEntryRemoved(eldest.getKey(), eldest.getValue());
					}
					return true;
				}
				return false;
			}
		};
	}
	
	public void put(K key, V value) {
		cache.put(key, value);
	}
	
	public V get(K key) {
		return cache.get(key);
	}
	
	public Collection<V> retrieveAll() {
		return cache.values();
	}
	
	public void setEntryRemovedListener(CacheEntryRemovedListener<K, V> removeListener) {
		this.removeListener = removeListener;
	}
}
