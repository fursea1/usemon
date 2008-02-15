package org.usemon.usageinfo;

import java.util.Date;

import junit.framework.TestCase;

public class UsageTest extends TestCase {

	public void testIsExpired() throws InterruptedException {
		Usage usage = new Usage(new MethodInfo(Info.COMPONENT_CUSTOM, "P1", "C1", "M1", "()"));
		Date expiration = usage.getExpirationTime();
		long t = System.currentTimeMillis() + (60*1000);
		Date d2 = new Date(t);
		System.err.println(expiration + " > " + d2);
		assertTrue("Expiration should happen within a minute ",d2.after(expiration));
	}
}
