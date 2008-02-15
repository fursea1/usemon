/**
 * Created 3. des.. 2007 11.30.23 by Steinar Overbeck Cook
 */
package org.usemon.service.ehcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.junit.Before;
import org.junit.Test;
import org.usemon.domain.J2eeLocation;
import org.usemon.service.util.Dimension;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class EhCacheExpTest {

	CacheManager cacheManager = CacheManager.getInstance();
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void simpleTest() throws InterruptedException {
		Cache cache = new Cache(Dimension.LOCATION.toString(), 
				3,	// max elements in memory 
				MemoryStoreEvictionPolicy.LRU, 
				false, // Overflow to disk, 
				null, // disk path
				false, // eternal
				1,	// timetoLiveSeconds
				1,	// timeToIdleseconds
				false,	// diskPersistent
				0,		// diskExpiryThreadIntervalSeconds
				null	// Registered event listeners
				);
		cacheManager.addCache(cache);
		J2eeLocation location = new J2eeLocation("P1","C1","S1");
		cacheManager.getCache(Dimension.LOCATION.toString()).put( new Element(location, new Integer(42)));
		// Attempts to look it up
		Element e = cache.get(location);
		Integer fkValue = (Integer) e.getObjectValue();
		assertEquals(new Integer(42), fkValue);
		
		// Lookup something different, which we should not find
		e = cache.get(new J2eeLocation("P1","C1", "S2"));
		assertNull(e);
		
		
		// Wait a minute to see if stuff gets evicted
		Thread.sleep(1050);
		e = cache.get(location);
		assertNull(e);

		// insert 1 entry more than the maximum
		for (int i=0; i < cache.getCacheConfiguration().getMaxElementsInMemory() + 1; i++) {
			J2eeLocation l = new J2eeLocation("L" + i, "CT", "S1");
			cache.put(new Element(l, new Integer(i)));
		}
		// The first entry should now have been evicted
		e = cache.get(new J2eeLocation("L0", "CT","S1"));
		assertNull(e);
		// While the second entry should still reside in the cahce
		e = cache.get(new J2eeLocation("L1", "CT","S1"));
		assertNotNull(e);
	}
	
	@Test
	public void testCreateDefaultCache() {
		cacheManager.addCache(Dimension.CLASS.toString());
		Cache cache = cacheManager.getCache(Dimension.CLASS.toString());
		assertEquals(10000,cache.getCacheConfiguration().getMaxElementsInMemory());
	}
}
