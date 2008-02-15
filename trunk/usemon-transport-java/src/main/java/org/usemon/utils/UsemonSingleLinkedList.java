/**
 * Created 20. des.. 2007 10.57.45 by Steinar Overbeck Cook
 */
package org.usemon.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementation of {@SingleLinkedList} for use with usemon
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class UsemonSingleLinkedList implements SingleLinkedList {

	/** UsemonSingleLinkedListIterator for our special single linked list implementation.
	 */
	private class UsemonSingleLinkedListIterator implements java.util.Iterator {
		SingleLinkedNode current;	// current element which will be returned by next()
		boolean beforeFirst = true;	// indicates whether we have started iteration or not.

		
		/** Constructor 
		 * @param node node from which iteration will start. This node is included as the first node
		 * upon invocation of {@link #next()}
		 */
		UsemonSingleLinkedListIterator(final SingleLinkedNode node) {
			if (node == null)
				throw new IllegalArgumentException("Invalid node: null");
			current = node;
		}
		
		public boolean hasNext() {
			// if we have not yet started there is another element available
			return (beforeFirst || (current.getNext() != null));
		}

		public Object next() {
			if (!beforeFirst)
				current = current.getNext();
			if (current == null)
				throw new NoSuchElementException();

			beforeFirst = false;
			return current;
		}
		
		public void remove() {
			// Removes the current node from the list
			UsemonSingleLinkedList.this.remove(current);
		}
	}

	// References the first node in the linked list, i.e. the element inserted last
	SingleLinkedNode head = null;

	long size = 0;

	protected final SingleLinkedNode DUMMY = new SingleLinkedNode() {
		SingleLinkedNode next = null;

		public SingleLinkedNode getNext() {
			return next;
		}

		public void setNext(SingleLinkedNode singleLinkedNode) {
		}

		public String toString() {
			return "DUMMY";
		}

	};

	public UsemonSingleLinkedList() {
		clear();
	}

	/** Adds another element to the head of the list */
	public void add(SingleLinkedNode newNode) {
		// Makes the new head node point to what used to be the head node
		if (size == 0)
			newNode.setNext(null);
		else
			newNode.setNext(head);

		// Makes new node the head of the list
		head = newNode;

		size++;
	}

	public void clear() {
		head = DUMMY;
		size = 0;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Removes an element from the list.
	 * @return <code>true</code> if the node was found and removed, <code>false</code> otherwise
	 */
	public boolean remove(SingleLinkedNode singleLinkedNode) {
		SingleLinkedNode previous = null;
		
		// If the supplied node is the head node, we must take special care.
		if (head.equals(singleLinkedNode)) {
			// If current size is 1, this is the only element in the list
			if (size == 1) {
				clear();
				return true;
			} else if (size > 1) { 
				// Make next node the head
				head = singleLinkedNode.getNext();
				size--;
				return true;
			}
		} else {
			// We are removing a node in the middle somewhere...
			previous = findPrevious(singleLinkedNode);
			if (previous != null) {
				previous.setNext(singleLinkedNode.getNext());
				size--;
				return true;
			}
		}
		return false;	// This indicates that the node was not found in the list.
	}

	/**
	 * Return element prior to the first node containing an item. Prior meaning
	 * the element which was added into the list after this one. I.e. we are
	 * looking for the element which references the supplied element with it's
	 * "next" property.
	 * 
	 * @param singleLinkedNode
	 *            the element to search for
	 * @return appropriate item if found. Otherwise, <em>null</em> is
	 *         returned.
	 */
	public SingleLinkedNode findPrevious(final SingleLinkedNode singleLinkedNode) {

		SingleLinkedNode currentNode = head;

		if (isEmpty())
			return null;

		// Inspects the next node see if the current node might be the parent
		while (currentNode.getNext() != null && !currentNode.getNext().equals(singleLinkedNode)) {
			currentNode = currentNode.getNext(); // Skips to next element in list
		}

		if (currentNode.getNext() == null)	// end of list
			return null;
		else
			return currentNode;
	}

	public long size() {
		return size;
	}

	/**
	 * Returns the node at the head of the list and removes it.
	 * 
	 * @return element at the head of the list, <code>null</code> if list is empty
	 */
	public SingleLinkedNode remove() {
		SingleLinkedNode result = null;

		if (isEmpty())
			return null;
		
		if (head.getNext() != null) {
			result = head;
			head = head.getNext();
			size--;
		} else {
			// if we are at the header node, empty the list
			head = DUMMY;
			size = 0;
		}
		return result;
	}

	/** Removes all elements after the supplied element, i.e. all elements which were added to the list before the supplied one.
	 * 
	 * @param singleLinkedNode
	 * @return number of elements removed
	 */
	public int removeAllAfter(SingleLinkedNode singleLinkedNode) {
		SingleLinkedNode current = singleLinkedNode;

		// Figures out how many elements will be removed
		int count = 0;
		while (current.getNext() != null) {
			current = current.getNext();
			count++;
		}
		size -= count;
		// Cuts the rope, bye bye
		singleLinkedNode.setNext(null);
		return count;
	}

	/** Provides a simple java iterator, which will guide you through the entire
	 * list from head to tail.
	 * @return an iterator
	 */
	public java.util.Iterator iterator() {
		return new UsemonSingleLinkedListIterator(head);
	}
	
	/** Provides an iterator which will let you iterate through all entries inserted into the
	 * list <b>before</b> the supplied node, i.e. all entries added into the list before the supplied one.
	 * 
	 * @param singleLinkedNode starting point in list.
	 * @return iterator as described above
	 */
	public UsemonSingleLinkedListIterator getIteratorForAllAfter(SingleLinkedNode singleLinkedNode) {
		if (singleLinkedNode.getNext() != null)
			return new UsemonSingleLinkedListIterator(singleLinkedNode.getNext());
		else
			return new UsemonSingleLinkedListIterator(singleLinkedNode);
	}

	public Iterator iterator(SingleLinkedNode singleLinkedNode) {
		return new UsemonSingleLinkedListIterator(singleLinkedNode);
	}
}