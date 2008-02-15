/**
 * Created 3. jan.. 2008 08.45.42 by Steinar Overbeck Cook
 */
package com.usemon.agent.registry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.usemon.usageinfo.Usage;
import org.usemon.utils.SingleLinkedList;
import org.usemon.utils.UsemonSingleLinkedList;

/**
 * Holds a set of {@link Usage} objects. This implementation uses a hash map and a custom implementation of a single linked list in
 * order to cater for fastest possible get() and iteration operations.
 * 
 * <p>
 * The rationale for this class is to avoid synchronization of two different data structures within the {@link Registry} code.
 * <p>
 * This implementation is not thread safe.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class UsageCollectionSingleLinkedListImpl implements UsageCollection {

	// Holds the usage objects in a map, allowing for fast access using a key
	Map map = new HashMap();
	// Holds the same usage objects in a single linked list in order to
	// improve performance when iterating over the collection
	SingleLinkedList linkedList = new UsemonSingleLinkedList();

	/**
	 * Provides the number of {@link Usage} objects in the collection.
	 * 
	 * @see com.usemon.agent.registry.UsageCollection#size()
	 */
	public int size() {
		if (map.size() != linkedList.size())
			throw new IllegalStateException("The two underlying collections are not in synch, ll=" + linkedList.size() + ", map=" + map.size());
		return map.size();
	}

	/**
	 * Determines if the Usage object with the given key exists in the colleciton.
	 * 
	 * @see com.usemon.agent.registry.UsageCollection#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	/**
	 * Retrieves the Usage object with the given key from the collection.
	 * 
	 * @see com.usemon.agent.registry.UsageCollection#get(java.lang.String)
	 */
	public Usage get(String key) {
		return (Usage) map.get(key);
	}

	/**
	 * Inserts the given Usage object into the collection.
	 * 
	 * @see com.usemon.agent.registry.UsageCollection#put(org.usemon.usageinfo.Usage)
	 */
	public void put(Usage usage) {
		map.put(usage.getKey(), usage);
		linkedList.add(usage);
	}

	/**
	 * Provies an iterator over the Usage objects in the collection from and including the given object.
	 *
	 * @return an iterator or <code>null</code> if the given Usage object does not exist in the collection
	 * 
	 * @see com.usemon.agent.registry.UsageCollection#iterator(org.usemon.usageinfo.Usage)
	 */
	public Iterator iterator(Usage usage) {
		return new UsageCollectionIterator(usage);
	}

	/**
	 * Removes all the entries in the collection.
	 *  
	 * @see com.usemon.agent.registry.UsageCollection#clear()
	 */
	public void clear() {
		map.clear();
		linkedList.clear();
	}

	/**
	 * Removes the given Usage object from the collection.
	 *  
	 * @see com.usemon.agent.registry.UsageCollection#remove(org.usemon.usageinfo.Usage)
	 */
	public void remove(Usage usage) {
		map.remove(usage.getKey());
		linkedList.remove(usage);
	}

	/**
	 * Provides an iterator for all the items held in the collection
	 *  
	 * @see com.usemon.agent.registry.UsageCollection#iterator()
	 */
	public Iterator iterator() {
		return new UsageCollectionIterator();
	}

	
	/**
	 * Implementation of the {@link Iterator} for this implementation of the collection.
	 * 
	 * @author t547116 (Steinar Overbeck Cook)
	 *
	 */
	private class UsageCollectionIterator implements Iterator {

		Iterator iterator;
		Usage current = null;

		UsageCollectionIterator() {
			iterator = linkedList.iterator();
		}

		UsageCollectionIterator(Usage usage) {
			iterator = linkedList.iterator(usage);
		}

		public boolean hasNext() {
			return iterator.hasNext();
		}

		public Object next() {
			current = (Usage) iterator.next();
			return current;
		}

		public void remove() {
			linkedList.remove(current);
			map.remove(current.getKey());
		}

	}
}
