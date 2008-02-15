/**
 * 
 */
package org.usemon.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.TimeStampHelper;
import org.usemon.service.dao.DimensionalDao;
import org.usemon.service.util.Dimension;

/**
 * Note! JUnit testing the Dimensional Cache manager requires special considerations, as it
 * is implemented as a singleton!
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class DimensionCacheManagerImplTest {

	private static final Logger log = LoggerFactory.getLogger(DimensionCacheManagerImplTest.class);
	
	private static final int UNIVERSAL_ANSWER = 42;
	DimensionCacheManagerImpl dim = null;
	protected DataSource mockDs;
	private DimensionalDao mockDao;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mockDs = createMock(DataSource.class);
		mockDao = createMock(DimensionalDao.class);
		
		dim = new DimensionCacheManagerImpl(mockDao);
	}

	


	/**
	 * Test method for {@link org.usemon.service.DimensionCacheManagerImpl#getForeignKeyForValue(org.usemon.service.util.Dimension, java.lang.Object)}.
	 */
	@Test
	public void testGetForeignKeyForValue() {
		String locValue = "L1";
		// The internal logic should attempt to find it in the DBMS first, then
		// create it and finally return it.
		expect(mockDao.findForeginKeyInDbms(Dimension.LOCATION, locValue)).andReturn(0);
		expect(mockDao.createDimensionalValue(Dimension.LOCATION, locValue)).andReturn(UNIVERSAL_ANSWER);
		replay(mockDao);
		int i = dim.getForeignKeyForValue(Dimension.LOCATION, locValue);
		assertEquals(0, dim.getCacheHits());
		assertEquals(42,i);
		
		// Second time around, we should find it in the cache.
		int i2 = dim.getForeignKeyForValue(Dimension.LOCATION, locValue);
		assertEquals(1, dim.getCacheHits());
		assertEquals(i,i2);
		verify(mockDao);
	}

	/**
	 * Test method for {@link org.usemon.service.DimensionCacheManagerImpl#createDimensionalValueInDbmsAndCache(org.usemon.service.util.Dimension, java.lang.Object)}.
	 * <strong>NOTE! Since the object being tested is a singleton, state is persisted between each test.
	 * 
	 * @throws SQLException 
	 */
	@Test
	public void testCreateDimensionalValue() throws SQLException {

		String locationDimValue = "L1";

		dim.insertIntoCache(Dimension.LOCATION, UNIVERSAL_ANSWER, locationDimValue);
		log.debug("--- Running next test, attempting to find foreign key in cache");
		int fk = dim.findForeignKeyInCache(Dimension.LOCATION, locationDimValue);
		assertEquals(1, dim.getCacheHits());	// should have found the previous value in the cache
		assertEquals(UNIVERSAL_ANSWER, fk);
		
		// Give it another go, by invoking the protected method....
		expect(mockDao.createDimensionalValue(Dimension.LOCATION, locationDimValue)).andReturn(UNIVERSAL_ANSWER);
		replay(mockDao);
		dim.createDimensionalValueInDbmsAndCache(Dimension.LOCATION, locationDimValue);
		verify(mockDao);
		
		// Resets the mock in order to verify that we hit the cache for the third time.
		reset(dim.getDimensionalDao());
		fk = dim.getForeignKeyForValue(Dimension.LOCATION, locationDimValue);
		assertEquals(UNIVERSAL_ANSWER, fk);
		assertEquals(1, dim.getRequests());
		assertEquals(2, dim.getCacheHits());
	}
	
	/** Verifies that time values are handled properly */
	@Test
	public void testFindTimeValues() {
		Date dt = new Date();
		TimeStampHelper.modifyTimeStampGranularity(dt);
		expect(mockDao.findForeginKeyInDbms(Dimension.TIME, dt)).andReturn(UNIVERSAL_ANSWER+1);
		
		replay(mockDao);
		// Attempt to find the first value
		int fk = dim.getForeignKeyForValue(Dimension.TIME, dt);
		assertEquals(UNIVERSAL_ANSWER+1, fk);
		
		int hits = dim.getCacheHits();
		// play it again...
		int fk2 = dim.getForeignKeyForValue(Dimension.TIME, dt);
		
		// Same time next day....
		reset(mockDao);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date dt2 = cal.getTime();
		expect(mockDao.findForeginKeyInDbms(Dimension.TIME, dt2)).andReturn(UNIVERSAL_ANSWER+1);
		replay(mockDao);
		TimeStampHelper.modifyTimeStampGranularity(dt2);
		dim.getForeignKeyForValue(Dimension.TIME, dt2);
		
		assertEquals(fk,fk2);
		assertEquals(hits+1, dim.getCacheHits());
	}

}
