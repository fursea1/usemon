/**
 * Created 5. des.. 2007 15.19.20 by Steinar Overbeck Cook
 */
package com.usemon.agent.registry;

import java.util.Date;

import org.usemon.usageinfo.Info;
import org.usemon.usageinfo.Usage;

import junit.framework.TestCase;

/**
 * Ensures that the {@link Registry} class works as expected.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class RegistryTest extends TestCase {



	/**
	 * Test method for
	 * {@link com.usemon.agent.registry.Registry#ensureUsage(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 * <p>
	 * Verifies that adding two successive invocations within a short time frame, returns the same usage object. Furthermore we
	 * verify that an expired usage object is sent away.
	 * 
	 * @throws InterruptedException
	 *             if the sleep fails.
	 */
	public void testEnsureUsage() throws InterruptedException {
		Usage u1 = Registry.ensureUsage(Info.COMPONENT_CUSTOM, "P1", "C1", "M1", ";v");
		Usage u2 = Registry.ensureUsage(Info.COMPONENT_CUSTOM, "P1", "C1", "M1", ";v");
		assertEquals(u1, u2);
		System.err.println("Will ship objects after " + u2.getExpirationTime());
		// Computes the wake up time with an extra second for safety margin
		Date wakeUp = new Date(u2.getExpirationTime().getTime() + (1000));

		// Wait until the usage object is ready for shipment
		long sleepPeriod = (wakeUp.getTime() - System.currentTimeMillis() + 10);
		Thread.sleep(sleepPeriod); // waits for a minute

		assertTrue(Registry.getUsageCollectionSize() > 0);
		// The usage object should be ready for shipment now
		assertTrue(u2.isExpired());
		// Registers another invocation, which should cause the original object to be published
		Usage u3 = Registry.ensureUsage(Info.COMPONENT_CUSTOM, "P1", "C1", "M1", ";v");
		assertNotSame(u2, u3); // A new Usage object should have been created, since previous has been shipped
		assertFalse(u3.isExpired());

		assertEquals("Only the latest usage object should be present now", 1, Registry.getUsageCollectionSize());
	}

	/**
	 * Verifies that when activity is registered for an expired usage object, all other usage objects which have expired are sent
	 * off at the same time.
	 * 
	 * @throws InterruptedException
	 */
	public void testAllUsageObjectsShipped() throws InterruptedException {
		for (int i = 0; i < 1000; i++) {
			Registry.ensureUsage(Info.COMPONENT_CUSTOM, "p" + i, "c" + i, "map+1", ";v");
		}

		Usage u1 = Registry.ensureUsage(Info.COMPONENT_CUSTOM, "P1", "C1", "M1", ";v");
		Date wakeUp = new Date(u1.getExpirationTime().getTime() + 1000);
		long sleepPeriod = wakeUp.getTime() - System.currentTimeMillis() + 1000;
		Thread.sleep(sleepPeriod);
		Usage u2 = Registry.ensureUsage(Info.COMPONENT_CUSTOM, "P1", "C1", "M1", ";v");
		assertNotSame("Usage object not shipped after expiry", u2, u1);
		assertEquals("All but the last usage object should have been shipped", 1, Registry.getUsageCollectionSize());
	}
}
