/**
 * 
 */
package org.usemon.integrationtest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.usemon.domain.UsemonServiceLocator;

/**
 * @author t514257
 *
 */
public class UsemonServiceProviderImplTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Test method for {@link org.usemon.service.UsemonServiceProviderImpl#deputoService()}.
	 */
	@Test
	public void testDeputoService() {
		UsemonServiceLocator.getDeputoService();
	}

	/**
	 * Test method for {@link org.usemon.service.UsemonServiceProviderImpl#invocationService()}.
	 */
	@Test
	public void testInvocationService() {
		UsemonServiceLocator.getInvocationService();
	}

	/**
	 * Test method for {@link org.usemon.service.UsemonServiceProviderImpl#dimensionalQueryService()}.
	 */
	@Test
	public void testDimensionalQueryService() {
		UsemonServiceLocator.getDimensionalQueryService();
	}

}
