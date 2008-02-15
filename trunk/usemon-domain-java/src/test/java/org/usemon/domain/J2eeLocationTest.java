/**
 * Created 5. nov.. 2007 13.29.41 by Steinar Overbeck Cook
 */
package org.usemon.domain;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class J2eeLocationTest {

	/**
	 * Test method for {@link org.usemon.domain.J2eeLocation#J2eeLocation(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testJ2eeLocationWith3components() {
		J2eeLocation location = new J2eeLocation("P1", "CLUSTER1", "SERVER1");
		assertEquals("P1", location.getPlatformName());
		assertEquals("CLUSTER1", location.getClusterName());
		assertEquals("SERVER1", location.getServerName());
	}

	/**
	 * Test method for {@link org.usemon.domain.J2eeLocation#J2eeLocation(java.lang.String)}.
	 */
	@Test
	public void testJ2eeLocationString() {
		J2eeLocation l1 = new J2eeLocation("server1");
		assertEquals(J2eeLocation.DEFAULT, l1.getPlatformName());
		assertEquals(J2eeLocation.DEFAULT, l1.getClusterName());
		assertEquals("server1", l1.getServerName());
		
		l1 = new J2eeLocation("cluster1.server1");
		assertEquals(J2eeLocation.DEFAULT, l1.getPlatformName());
		assertEquals("cluster1", l1.getClusterName());
		assertEquals("server1", l1.getServerName());
		
		l1 = new J2eeLocation("platform1.cluster1.server1");
		assertEquals("platform1", l1.getPlatformName());
		assertEquals("cluster1", l1.getClusterName());
		assertEquals("server1", l1.getServerName());
		
		try {
			l1 = new J2eeLocation("a.b.c.d");
			fail("4 components in location should have failed");
		} catch (RuntimeException e) {
			// as expected
		}
	}
}
