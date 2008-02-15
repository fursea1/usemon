package com.usemon.agent.registry;

import java.lang.ref.ReferenceQueue;
import java.util.Iterator;

import junit.framework.TestCase;

public class SoftReferenceTest extends TestCase {

	private static final int COUNT = 10000;
	Runtime runtime = null;
	private SoftHashMap m;
	ReferenceQueue referenceQueue;
	protected void setUp() throws Exception {
		super.setUp();
		runtime = Runtime.getRuntime();
		m = new SoftHashMap();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSoftReference() {
		
		long end = System.currentTimeMillis() + 1000;// 5*60*1000;
		int i = 0;
		do {
			i++;
			allocateMemory(i,64*1024);
			
		} while (System.currentTimeMillis() < end);
	}

	
	public void testSoftIterator() {
		doAllocation();
		
		int successCount = 0;
		for (Iterator iterator = m.softIterator(); iterator.hasNext();) {
			Object o =  iterator.next();
			assertTrue(o instanceof MemoryHog);
			successCount++;
		}
		assertTrue(m.getRemoveCount()>0);
		System.err.println("SeccessCount: "+successCount);
		System.err.println("RemoveCount: "+m.getRemoveCount());
		assertEquals(successCount+m.getRemoveCount(), COUNT);
	}

	public void testContainsKey() {
		doAllocation();
		int count = 0;
		for(int i=0;i<COUNT;i++) {
			if(m.containsKey(new Integer(i))) {
				count++;
			}
		}
		System.err.println("Count: "+count);
		assertEquals(COUNT-m.getRemoveCount(), count);		
	}
	
	private void doAllocation() {
		for (int i = 0; i < COUNT; i++) {
			allocateMemory(i, 64*1024);
		}
	}
	
	private void allocateMemory(int n, int size) {
		MemoryHog mhog = new MemoryHog(n, size);
		m.put(new Integer(mhog.getNumber()),mhog);
	}

	public class MemoryHog {
		int number;
		Byte[] b;
		
		public MemoryHog(int number, int size) {
			this.number= number;
			b =  new Byte[size];
		}

		public int getNumber() {
			return number;
		}
	}
}
