/**
 * Created 5. des.. 2007 15.19.20 by Steinar Overbeck Cook
 */
package com.usemon.agent.registry;

import java.util.Date;

import org.usemon.usageinfo.Info;
import org.usemon.usageinfo.Usage;

import com.usemon.agent.publisher.Publisher;

import junit.framework.TestCase;

/**
 * Verifies the performance of the code responsible for sending the usage objects over
 * to the {@link Publisher}.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class RegistryPerformanceTest extends TestCase {

	private static final int NUMBER_OF_ENTRIES = 20000;

	public void testAllUsageObjectsShipped() throws InterruptedException {
		for (int i=0; i < NUMBER_OF_ENTRIES; i++) {
			Registry.ensureUsage(Info.COMPONENT_CUSTOM, "p" +i, "c"+i, "map+1", ";v");
		}
		
		Usage u1 = Registry.ensureUsage(Info.COMPONENT_CUSTOM, "P1", "C1", "M1", ";v");
		Date wakeUp = new Date(u1.getExpirationTime().getTime() + 1000);
		long sleepPeriod = wakeUp.getTime() - System.currentTimeMillis();
		Thread.sleep(sleepPeriod);
		long start = System.currentTimeMillis();
		Usage u2 = Registry.ensureUsage(Info.COMPONENT_CUSTOM, "P1", "C1", "M1", ";v");
		long stop = System.currentTimeMillis();
		long elapsed = stop - start;
		System.err.println("FLushing " + NUMBER_OF_ENTRIES + " usage objects took " + elapsed + "ms");
		assertNotSame("Usage object not shipped after expiry", u2, u1);
		assertEquals("All but the last usage object should have been shipped", 1, Registry.getUsageCollectionSize());
	}
}
