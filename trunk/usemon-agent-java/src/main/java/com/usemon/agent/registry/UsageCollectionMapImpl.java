/**
 * Created 8. jan.. 2008 12.08.18 by Steinar Overbeck Cook
 */
package com.usemon.agent.registry;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.usemon.usageinfo.Usage;

/**
 * This implementation is based upon using a single {@link WeakHashMap} as the backing store
 * for the Usage objects.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class UsageCollectionMapImpl implements UsageCollection {

	Map m = new SoftHashMap();
	
	public UsageCollectionMapImpl() {
		
	}
	
	/**
	 * @see com.usemon.agent.registry.UsageCollection#clear()
	 */
	public void clear() {
		m.clear();
	}

	/** 
	 * @see com.usemon.agent.registry.UsageCollection#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key) {
		return m.containsKey(key);
	}

	/**
	 * @see com.usemon.agent.registry.UsageCollection#get(java.lang.String)
	 */
	public Usage get(String key) {
		return (Usage) m.get(key);
	}

	/**
	 * @see com.usemon.agent.registry.UsageCollection#iterator(org.usemon.usageinfo.Usage)
	 */
	public Iterator iterator(Usage usage) {
		return iterator();
	}

	/**
	 * @see com.usemon.agent.registry.UsageCollection#iterator()
	 */
	public Iterator iterator() {
		return ((SoftHashMap) m).softIterator();
	}

	/**
	 * @see com.usemon.agent.registry.UsageCollection#put(org.usemon.usageinfo.Usage)
	 */
	public void put(Usage usage) {
		m.put(usage.getKey(), usage);
	}

	/**
	 * @see com.usemon.agent.registry.UsageCollection#remove(org.usemon.usageinfo.Usage)
	 */
	public void remove(Usage usage) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see com.usemon.agent.registry.UsageCollection#size()
	 */
	public int size() {
		return m.size();
	}

}
