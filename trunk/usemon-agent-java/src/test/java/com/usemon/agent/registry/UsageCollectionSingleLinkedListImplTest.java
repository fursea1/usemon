package com.usemon.agent.registry;

import java.util.Iterator;

import org.usemon.usageinfo.MethodInfo;
import org.usemon.usageinfo.ResourceInfo;
import org.usemon.usageinfo.Usage;

import junit.framework.TestCase;

public class UsageCollectionSingleLinkedListImplTest extends TestCase {

	private static final int ITEM_COUNT = 4;
	UsageCollection c = null;
	Usage usage = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		c = new UsageCollectionSingleLinkedListImpl();
		for (int i=0; i < ITEM_COUNT-1; i++) {
			Usage u = new Usage(new MethodInfo(ResourceInfo.COMPONENT_CUSTOM, "p"+i, "c"+i, "map"+i, ";v"));
			if (i==0)
				usage = u;
			
			c.put( u);
		}
		Usage u = new Usage(new ResourceInfo(ResourceInfo.COMPONENT_CUSTOM,"java.lang.Qeueue","jms://q1"));
		c.put(u);
	}

	public void testSize() {
		assertEquals(ITEM_COUNT, c.size());
	}

	public void testContainsKey() {
		assertTrue(c.containsKey(usage.getKey()));
	}

	public void testGet() {
		Usage u2 = c.get(usage.getKey());
		assertEquals(u2,usage);
		
	}

	public void testIterator() {
		int i = 0;
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			iter.next();
			i++;
		}
		assertEquals(ITEM_COUNT,i);
	}

	public void testClear() {
		c.clear();
		assertTrue(c.size() == 0);
	}

	public void testRemove() {
		c.remove(usage);
		assertEquals(ITEM_COUNT - 1, c.size());
	}

}
