/**
 * Created 15. nov.. 2007 11.11.44 by Steinar Overbeck Cook
 */
package org.usemon.domain;

import static org.junit.Assert.*;

import org.junit.Test;
import org.usemon.service.DeputoServiceImpl;

/**
 * Attempts to locate the various services and verifies that all dependencies
 * have been injected.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class UsemonServiceLocatorTest {

	/**
	 * Test method for
	 * {@link org.usemon.domain.UsemonServiceLocator#getDeputoService()}.
	 */
	@Test
	public void testGetDeputoService() {
		DeputoService deputoService = UsemonServiceLocator.getDeputoService();

		assertTrue(deputoService instanceof DeputoServiceImpl);

		DeputoServiceImpl d = (DeputoServiceImpl) deputoService;
		assertNotNull(d.getDataSource());
		assertNotNull(d.getDimensionCacheManager());

		DeputoServiceImpl d2 = (DeputoServiceImpl) UsemonServiceLocator.getDeputoService();
		// Ensures that we only create a single instance of the cache
		assertEquals(d.getDimensionCacheManager(), d2.getDimensionCacheManager());
		
		// Ensure that we only create a single instance of the deputo service
		assertEquals((DeputoServiceImpl)deputoService, d2);
	}

	/**
	 * Test method for
	 * {@link org.usemon.domain.UsemonServiceLocator#getInvocationService()}.
	 */
	@Test
	public void testGetInvocationService() {
		UsemonServiceLocator.getInvocationService();
	}

}
