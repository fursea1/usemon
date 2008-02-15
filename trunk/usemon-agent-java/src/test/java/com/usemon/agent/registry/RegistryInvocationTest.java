/**
 * Created 5. des.. 2007 15.19.20 by Steinar Overbeck Cook
 */
package com.usemon.agent.registry;

import junit.framework.TestCase;

import org.usemon.usageinfo.Info;

/**
 * Ensures that the {@link Registry} class works as expected.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class RegistryInvocationTest extends TestCase {

	/** Verifies that we can perform a number of invocations without
	 * bumbping into memory problems.
	 * 
	 * @throws InterruptedException
	 */
	public void testInvocation() throws InterruptedException {
		long end = System.currentTimeMillis() + (60 * 1000);
		do {
			for (int n = 0; n < 1000; n++) {
				Thread.sleep(1);
				for (int i = 0; i < 1000; i++)
					Registry.invocation(Info.COMPONENT_CUSTOM, "package" + i, "class" + i, "method" + i, ";v", "public", 15, "princip", null);
			}
		} while (System.currentTimeMillis() < end);
	}

}
