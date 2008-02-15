/**
 * Created 20. des.. 2007 10.09.19 by Steinar Overbeck Cook
 */
package org.usemon.utils;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface SingleLinkedNode {

	/** Provides the reference to the next node in the linked list */ 
	public SingleLinkedNode getNext();
	
	public void setNext(SingleLinkedNode singleLinkedNode);
}
