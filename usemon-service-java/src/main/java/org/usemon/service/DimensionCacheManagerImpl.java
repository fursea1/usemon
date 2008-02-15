/**
 * 
 */
package org.usemon.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.service.dao.DimensionalDao;
import org.usemon.service.util.Dimension;

import com.google.inject.Inject;

/**
 * This is the default (and only) implementation of the
 * {@link DimensionCacheManager}. This implementation relies upon the use of
 * <em>ehCache</em>.
 * <p>
 * The general use case case for transforming of data value representing a
 * dimension into it's corresponding foreign key, goes something like this:
 * <ol>
 * <li>Lookup the value in the in-memory cache and return if found.
 * <li>Lookup in the database, insert into cache and return if found.
 * <li>Create new entry in database, insert into cache and return new value.
 * </ol>
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class DimensionCacheManagerImpl implements DimensionCacheManager, Runnable {

	private static final Logger log = LoggerFactory.getLogger(DimensionCacheManagerImpl.class);

	private static final int FOREIGN_KEY_FOR_UNKNOWN_VALUE = 0;

	private DimensionalDao _dimensionalDao;

	// Creates the ehCache cache manager
	private CacheManager cacheManager;

	private int cacheHits = 0;
	private int requests = 0;

	/**
	 * Constructor, you should normally only have a single instance of this
	 * object running.
	 * 
	 * @param dimensionalDao
	 *            must have access to the data base somehow :-)
	 */
	@Inject
	public DimensionCacheManagerImpl(DimensionalDao dimensionalDao) {
		log.warn("Creating instance of DimensionalChacheManager, this message should only occur once, if you want effective caching :-)");
		_dimensionalDao = dimensionalDao;

		configureCacheManager();	

		Thread statusReporterThread = new Thread(this);
		statusReporterThread.setName("usemonCacheStats");
		statusReporterThread.setDaemon(true);
		statusReporterThread.start();
		if (log.isDebugEnabled()) {
			log.debug("Created cache statistics reporting thread");
		}
	}

	/**
	 * 
	 */
	private void configureCacheManager() {
		String ehCacheConfigfile = PropertyHelper.getEhCacheConfig();

		// the config file resides in the classpath
		if ("classpath".equals(ehCacheConfigfile)) {
			URL url = getClass().getClassLoader().getResource("ehcache.xml");
			if (url == null)
				throw new IllegalStateException("Unable to load file ehcache.xml from classpath");
			log.info("Configuring ehCache from classpath resource:" + url);
			cacheManager = CacheManager.create(url);
		} else {
			try {
				File f = new File(ehCacheConfigfile);
				if (!f.isFile() || !f.canRead()) {
					throw new IllegalStateException("Unable to locate ehCache config file:" + f.getCanonicalPath());
				}
				log.info("Configuring ehCache from " + f.getCanonicalPath());
				cacheManager = CacheManager.create(f.getCanonicalPath());
			} catch (IOException e) {
				throw new IllegalStateException("Unable to access file " + ehCacheConfigfile);
			}
		}
	}

	/**
	 * Provides the primary key for a value of the given dimension. This
	 * implementation works like this:
	 * <ol>
	 * <li>Check the in-memory cache using the lookupValue as the key
	 * <li>Try locating the value in the database for the given dimension. If
	 * found, shove it into the cache.
	 * <li>Create a new entry for the dimension and save that value in our
	 * cache
	 * </ol>
	 * {@inheritDoc}
	 */
	public int getForeignKeyForValue(Dimension dimension, Object lookupValue) {
		if (dimension == null)
			throw new IllegalArgumentException("argument 'dimension' is required");

		requests++;
		// provides the default n/a or "unknown" foreign key.
		if (lookupValue == null)
			return dimension.getForeignKeyForUnknownValue();

		int fkValue = FOREIGN_KEY_FOR_UNKNOWN_VALUE;

		// First looks it up in our cache...
		if ((fkValue = findForeignKeyInCache(dimension, lookupValue)) == FOREIGN_KEY_FOR_UNKNOWN_VALUE) {
			// .. then attempts locates the foreign key value in the database.
			fkValue = _dimensionalDao.findForeginKeyInDbms(dimension, lookupValue);

			// If not found in DBMS, create the dimensional value in DBMS and
			// insert into cache
			if (fkValue == FOREIGN_KEY_FOR_UNKNOWN_VALUE) {
				fkValue = createDimensionalValueInDbmsAndCache(dimension, lookupValue);
			} else
				// otherwise, insert the dbms entry into our cache.
				insertIntoCache(dimension, fkValue, lookupValue);
		}
		return fkValue;
	}

	/**
	 * Creates the new dimensional value by inserting another entry into the
	 * database and storing the new entry into the cache.
	 * 
	 * @param dimension
	 *            identifies the dimension
	 * @param newValue
	 *            the value we wish to create
	 * @return
	 */
	protected int createDimensionalValueInDbmsAndCache(Dimension dimension, Object newValue) {
		int fk = _dimensionalDao.createDimensionalValue(dimension, newValue);
		if (fk < 1)
			throw new IllegalStateException("Invalid foreign key '" + fk + "'from database for value '" + newValue + "' in dimension " + dimension);
		insertIntoCache(dimension, fk, newValue);
		return fk;
	}

	/**
	 * Locates a dimensional value in our cache.
	 * 
	 * @param dimension
	 *            dimension we are working in
	 * @param lookupValue
	 *            the concrete value
	 * @return integer foreign key
	 */
	protected int findForeignKeyInCache(Dimension dimension, Object lookupValue) {
		// Locates an existing cache for this dimension or creates it.
		Cache dimCache = getCacheForDimension(dimension);

		int fk = FOREIGN_KEY_FOR_UNKNOWN_VALUE;

		// Lookup the
		Element e = dimCache.get(lookupValue);
		if (e != null) {
			fk = (Integer) e.getValue();
			cacheHits++;
		} else if (e == null) {
			if (log.isDebugEnabled()) {
				log.debug("No cache hit for value " + lookupValue + " in cache for " + dimension);
			}
		}
		return fk;
	}

	/**
	 * Retrieves existing cache or creates brand new one.
	 * 
	 * @param dimension
	 *            represents the dimension for which the cache should be located
	 * @return reference to the cache for this dimension
	 */
	protected Cache getCacheForDimension(Dimension dimension) {
		String cachename = dimension.toString();
		Cache cache = null;
		// Creates the cache for given dimension if it does not exist
		if (!cacheManager.cacheExists(cachename)) {
			cacheManager.addCache(cachename);
		}

		cache = cacheManager.getCache(cachename);

		assert cache != null : "cache should either exist or be automagically created";

		return cache;
	}

	/** Inserts a new dimensional value into our cache */
	/**
	 * @param dimension
	 *            the dimension used to select the cache
	 * @param fk
	 *            foreign key to insert as the value in the cache
	 * @param value
	 *            dimension value for which the foreign key should be stored
	 */
	protected void insertIntoCache(Dimension dimension, int fk, Object value) {
		Cache dimCache = getCacheForDimension(dimension);
		if (log.isDebugEnabled()) {
			log.debug("Inserting entry '" + value + "' = " + fk + " into cache for dimension " + dimension);
		}
		dimCache.put(new Element(value, new Integer(fk)));
	}

	public void clearStatistics() {
		cacheHits = 0;
		requests = 0;
	}

	public int getCacheHits() {
		return cacheHits;
	}

	public int getRequests() {
		return requests;
	}

	public DimensionalDao getDimensionalDao() {
		return _dimensionalDao;
	}

	public void setDimensionalDao(DimensionalDao dao) {
		_dimensionalDao = dao;
	}

	/**
	 * Statistics reporter main method.
	 */
	public void run() {
		int reportingInterval = PropertyHelper.getCacheReportingInterval();
		if (log.isInfoEnabled())
			log.info("Reporting on cache statistics every " + reportingInterval + " milliseconds");
		while (true) {
			try {
				// Reports on the cache statistics
				reportCacheStatistics();

				Thread.sleep(reportingInterval); // Wait a minute
			} catch (InterruptedException e) {
				if (log.isDebugEnabled()) {
					log.debug("Status reporting thread interrupted - halting...");
				}
				reportCacheStatistics();
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e1) {
					break; // attempt to simply break out.
				}
			}
		}
	}

	/** Performs the actual statistics reporting from the cache 
	 * 
	 */
	private void reportCacheStatistics() {
		log.info("There are " + cacheManager.getCacheNames().length + " active dimension caches");
		for (String cacheName : cacheManager.getCacheNames()) {
			Cache cache = cacheManager.getCache(cacheName);
			Statistics statistics = cache.getStatistics();
			if (log.isInfoEnabled()) {
				log.info(statistics.toString());
			}
		}
	}
}
