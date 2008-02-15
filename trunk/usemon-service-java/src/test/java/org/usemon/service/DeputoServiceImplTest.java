/**
 * 
 */
package org.usemon.service;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class DeputoServiceImplTest {

	DeputoServiceImpl deputoServiceImpl = null;
	
	@Before
	public void setUp() {
		DimensionCacheManager dimensionCacheManager = EasyMock.createMock(DimensionCacheManager.class);
		deputoServiceImpl = new DeputoServiceImpl(dimensionCacheManager);
	}
	
	@Test
	public void simpleTest() {
		Assert.assertNotNull(deputoServiceImpl.dimensionCacheManager);
	}
}
