/**
 * Created 20. des.. 2007 10.14.45 by Steinar Overbeck Cook
 */
package org.usemon.utils;

import java.util.Iterator;


/**
 * Single linked list which works as a FIFO queue.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public interface SingleLinkedList {

	/** Returns true if empty; else false */
	boolean isEmpty();

	/** Removes all items */
	void clear();

	/** Inserts new entry at the head of the list*/
	void add(SingleLinkedNode singleLinkedNode);

	/** Removes the given node from the list
	 * 
	 * @return <code>true</code> if node was removed, false otherwise
	 */
	boolean remove(SingleLinkedNode singleLinkedNode);

	/** Number of elements in list */
	public long size();

	/**
	 * Returns the node at the head of the list and removes it. Using this method will 
	 * make the list work as a LIFO queue.
	 * 
	 * @return element at the head of the list, <code>null</code> if list is empty
	 */
	public SingleLinkedNode remove();

	/** Provides a simple java iterator, which will guide you through the entire
	 * list from head to tail.
	 * @return an iterator
	 */
	public java.util.Iterator iterator();

	/** Provides an iterator which will start iterating through the collection 
	 * from the given usage object (inclusive)
	 * 
	 * @param usage
	 * @return
	 */
	Iterator iterator(SingleLinkedNode singleLinkedNode);
}
