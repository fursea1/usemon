/**
 * 
 */
package org.usemon.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.usemon.domain.J2eeLocation;
import org.usemon.service.dao.DimensionalDao;
import org.usemon.service.dao.DimensionalDaoImpl;
import org.usemon.service.util.Dimension;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class DimensionCacheManagerImplIntegrationTest {

	private static final String LOCATION_TO_BE_CREATED_IN_THIS_TEST = "Location.it-test";
	private DimensionCacheManager cacheManager;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		DataSource ds = DbmsHelper.getDataSource();
		DimensionalDao dao = new DimensionalDaoImpl(ds);
		cacheManager = new DimensionCacheManagerImpl(dao);
	}

	/**
	 * Test method for {@link org.usemon.service.DimensionCacheManagerImpl#getForeignKeyForValue(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testGetForeignKeyForValue() {
		
		int fk = cacheManager.getForeignKeyForValue(Dimension.LOCATION, "unknown");
		assertEquals(1, fk);
		fk = cacheManager.getForeignKeyForValue(Dimension.LOCATION, new J2eeLocation(LOCATION_TO_BE_CREATED_IN_THIS_TEST));
		assertTrue("Location "+ LOCATION_TO_BE_CREATED_IN_THIS_TEST +" not found and not created", fk > 1);
		
	}

}
