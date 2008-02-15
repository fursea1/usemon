package com.usemon.agent.registry;

import java.util.Iterator;

import org.usemon.usageinfo.Usage;

/**
 * Collection of Usage objects to be transferred to the 
 * Publisher for multi casting.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface UsageCollection {

	/**
	 * Number of elements held in the collection. An exception is thrown 
	 * if the two underlying collections are not in synch.
	 * 
	 * @return number of items in the underlying collection
	 */
	public abstract int size();

	public abstract boolean containsKey(String key);

	/**
	 * Retrieves the Usage objects associated with the given key
	 * @param key
	 * @return
	 */
	public abstract Usage get(String key);

	/** Adds another entry into the collection */
	public abstract void put(Usage usage);

	/** Provides an iterator from the given usage object, including the one provided */
	public abstract Iterator iterator(Usage usage);

	/** Clears the underlying collection structures */
	public abstract void clear();

	/** Removes the supplied usage object from all underlying collections */
	public abstract void remove(Usage usage);

	/** Provides an iterator over all the Usage objects held in the collection */
	public abstract Iterator iterator();
}