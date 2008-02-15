/**
 * Created 5. des.. 2007 15.27.54 by Steinar Overbeck Cook
 */
package org.usemon.stresstest.agent;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class StressMakerTest extends TestCase {

	public void testEmpty() {
		
	}
	
	/**
	 * Test method for {@link org.usemon.stresstest.agent.StressMaker#generateEntry()}.
	 * 
	 * FIXME: This test fails due to memory usage problems. Fix later!
	 */
	public void XtestGenerateEntry() {
		Map<String, Integer> m = new HashMap<String, Integer>();
		long start = System.currentTimeMillis();
		do {
			String s = generateEntry();
			if (m.containsKey(s)) {
				Integer i = m.get(s);
				i++;
				m.put(s, i);
			} else
				m.put(s, 1);
		} while (System.currentTimeMillis() < start+60000);	// 1 minute
		
		int foundDuplicateEntries = 0;
		for (Integer i : m.values()) {
			if (i > 1)
				foundDuplicateEntries++;
		}
		assertTrue(foundDuplicateEntries > 0);
	}

	
	private String generateEntry() {
		StringBuilder sb = new StringBuilder();
		sb.append(StressMaker.generatePackageName());
		sb.append(StressMaker.generateClassName());
		sb.append(StressMaker.generateMethodName());
		return sb.toString();
	}
}
