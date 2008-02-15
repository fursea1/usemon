/**
 * Created 20. des.. 2007 11.18.14 by Steinar Overbeck Cook
 */
package org.usemon.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class UsemonSingleLinkedListTest extends TestCase {

	private static final int MANY_ITEMS = 100000;
	UsemonSingleLinkedList l;
	
	protected void setUp() throws Exception {
		super.setUp();
		l = new UsemonSingleLinkedList();
	}

	public void testEquality() {
		SimpleNode n1 = new SimpleNode();
		SimpleNode n2 = new SimpleNode();
		assertNotSame(n1,n2);
	}
	/**
	 * Test method for {@link org.usemon.utils.UsemonSingleLinkedList#UsemonSingleLinkedList()}.
	 */
	public void testUsemonSingleLinkedList() {
		assertTrue(l.isEmpty());
		assertEquals(0, l.size);
		assertEquals(0,l.size());
	}

	
	public void testAdd() {
		SingleLinkedNode firstElement = new SimpleNode();
		l.add(firstElement);	// adds a single node

		// Head  should point to first element
		assertEquals(l.head,firstElement);
		
		// new element's next element should be null
		assertNull(firstElement.getNext());
		
		// List now contains a single element
		assertEquals(1,l.size);
		
		// adds a second element
		SingleLinkedNode secondElement = new SimpleNode();
		l.add(secondElement);
		
		// the head should now reference the last element added
		assertEquals(l.head, secondElement);
		assertNotNull(l.head.getNext());
		
		// The head should reference the first element added
		assertEquals(l.head.getNext(), firstElement);
		
		SingleLinkedNode previous = l.findPrevious(firstElement);
		assertNotNull("Previous node of last element should not be null", previous);
		
		assertEquals(previous, secondElement);
	}
	
	public void testFindPrevious() {
		SimpleNode n1 = new SimpleNode();
		SimpleNode n2 = new SimpleNode();
		l.add(n1);
		l.add(n2);
		assertEquals(n2.getNext(),n1);
		SingleLinkedNode prev = l.findPrevious(n1);

		assertTrue("n2 does not equal previous", n2.equals(prev));
		
		assertEquals(prev, n2);
		
		SimpleNode n3 = new SimpleNode();
		prev = l.findPrevious(n3);
		assertNull(prev);
	}
	
	public void testRemoveWithArguments() {
		ArrayList al = new ArrayList();
		for (int i=0; i < 10; i++) {
			SimpleNode n = new SimpleNode();
			al.add(n);
			l.add(n);
		}
		for (int i=0; i < 10; i++) {
			SimpleNode node = (SimpleNode) al.get(i);
			boolean rc = l.remove(node);
			assertTrue("Unable to remove element " + i, rc);
			assertEquals("Failure for iteration " + i, l.size(), 10-1-i);
		}
		assertTrue(l.isEmpty());
		assertEquals(l.head, l.DUMMY);
	}
	
	public void testRemove() {
		// Adds 10 nodes 
		for (int i=0; i < 10; i++) {
			 l.add(new SimpleNode());
		 }
		 assertEquals(10, l.size());
		 assertFalse(l.isEmpty());
		 
		 for (int i=0; i < 10; i++) {
			 l.remove();
			 assertEquals("i=" + i, 10-i-1, l.size());
		 }
	}
	
	
	public void testRemoveAllAfter() {
		for (int i=0; i < 10; i++) {
			l.add(new SimpleNode());
		}
		
		SingleLinkedNode n11 = new SimpleNode();
		l.add(n11);
		int c = l.removeAllAfter(n11);
		assertEquals(1,l.size());
		assertEquals(10,c);
		
	}
	
	/** Verifies that the special iterator which provides all elements after the given 
	 * element.
	 */
	public void testAllAfterIterator() {
		for (int i=0; i < 10; i++) {
			l.add(new SimpleNode());
		}
		
		SingleLinkedNode n11 = new SimpleNode();
		l.add(n11);
		
		Iterator iter = l.getIteratorForAllAfter(n11);
		int i = 0;
		while (iter.hasNext()) {
			SingleLinkedNode n = (SingleLinkedNode) iter.next();
			assertNotNull(n);
			i++;
		}
		assertEquals(10,i);
	}
	
	public void testPerformanceOfIterator() {
		for (int i=0; i < MANY_ITEMS; i++) {
			l.add(new SimpleNode());
		}
		
		// Step 1 - using our linked list
		long start = System.currentTimeMillis();
		int i = 0;
		for(Iterator iter = l.iterator(); iter.hasNext();) {
			iter.next();
			i++;
		}
		assertEquals("Problem with iterator", MANY_ITEMS, i);
		long end = System.currentTimeMillis();
		long slElapsed = end - start;
		System.err.println("LL:" + slElapsed);
		assertTrue("Iterating " + MANY_ITEMS + " took more than a minute",slElapsed < 1000);	// 
		
		// Step 2 - using a hash map
		Map m = new HashMap();
		for (i=0; i < MANY_ITEMS;i++) {
			String key = "Package" + i + ".pVery" + i + "LongKeyyyyyyyyyyylakjsfh%dakjs%dhlakjsfhd%dlakjsfhalkjsfhlksfhlaksfhalkf." + i;
			m.put(key, new SimpleNode());
		}
		
		start=System.currentTimeMillis();
		
		for (Iterator iterator = m.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry	e =  (Entry) iterator.next();
			e.getValue();
		}
		end = System.currentTimeMillis();
		long mapElapsed = end - start;
		System.err.println("HashMap:" + mapElapsed);
		assertTrue("mapElapsed: " + mapElapsed + ", linkedList elapsed:" + slElapsed,mapElapsed >= slElapsed );
	}
	
	
	public void testClear() {
		for (int i=0; i < 10; i++) {
			l.add(new SimpleNode());
		}
		l.clear();
		assertEquals(0,l.size());
		assertTrue(l.isEmpty());
	}
	
	public void testMemoryLeak() {
		memstatus();
		
		for (int i=0; i < MANY_ITEMS; i++) {
			l.add(new SimpleNode());
		}
		l.clear();

		memstatus();
		
		assertEquals(l.head, l.DUMMY);
		assertTrue(l.isEmpty());
		assertEquals(0,l.size());
	}

	/**
	 * Creates a sample list and removes all the itmes, one by one, using 
	 * the iterator.
	 */
	public void testIteratorRemove() {
		
		SimpleNode simpleNode;
		for (int i=0; i < 1000; i++) {
			 simpleNode = new SimpleNode();
			l.add(simpleNode);
		}
		
		for (Iterator iter=l.iterator(); iter.hasNext();) {
			iter.next();
			iter.remove();
		}
		assertTrue(l.isEmpty());

	}
	
	/**
	 * Verifies that removing all entries from a given node, will remove all
	 * entities added <b>before</b> the given node.
	 */
	public void testIteratorRemoveAllAddedBefore() {
		
		SingleLinkedNode node1 =null, simpleNode = null;
		
		for (int i=0; i < 3; i++) {
			simpleNode = new SimpleNode();
			if (i==1)
				node1 = simpleNode;	// Saves the second element, i.e. the 1st element counting from 0
			
			l.add(simpleNode);
		}		
		
		for (Iterator iter=l.iterator(node1); iter.hasNext();) {
			iter.next();
			iter.remove();
		}
		assertEquals(1, l.size());
		
	}
	
	/**
	 * 
	 */
	private void memstatus() {
//		System.err.println("Free:" + Runtime.getRuntime().freeMemory());
//		System.err.println("maxmemory:" +Runtime.getRuntime().maxMemory());
//		System.err.println("totalMemory:" + Runtime.getRuntime().totalMemory());
	}
	
	public static class SimpleNode implements SingleLinkedNode {
		SingleLinkedNode next;
		long value = 0;
		static long counter = 0;
		public SimpleNode() {
			value = counter++;
		}
		
		public SingleLinkedNode getNext() {
			return next;
		}

		public void setNext(SingleLinkedNode singleLinkedNode) {
			next = singleLinkedNode;
		}
		
		public String toString() { return value + ""; }
	}
}
