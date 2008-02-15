/**
 * 
 */
package org.usemon.service;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.DeputoService;
import org.usemon.domain.HeapObservation;
import org.usemon.domain.InvocationObservation;
import org.usemon.domain.MethodObservation;
import org.usemon.domain.Observation;
import org.usemon.domain.ObservationType;
import org.usemon.domain.TimeStampHelper;
import org.usemon.service.dao.HeapObservationDao;
import org.usemon.service.dao.InvocationObservationDao;
import org.usemon.service.dao.MethodObservationDao;

import com.google.inject.Inject;

/**
 * Provides the services as required to implement the {@link DeputoService}.
 * <p>
 * This implementation will obtain a database connection for each invocation of
 * the public methods and hang on to it until just before the method call
 * returns.
 * <p>
 * This implementation will pass the database connection to various DAOs etc.
 * during execution.
 * <p>
 * A reference to an instance of the cache manager will be obtained upon
 * instantiation of this class.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 */
public class DeputoServiceImpl implements DeputoService {

	Logger log = LoggerFactory.getLogger(DeputoServiceImpl.class);

	private DataSource dataSource;
	public DimensionCacheManager dimensionCacheManager = null;

	private MethodObservationDao methodObservationDao;
	private InvocationObservationDao invocationObservationDao;
	private HeapObservationDao heapObservationDao;
	
	@Inject
	public void setMethodObservationDao(MethodObservationDao methodObservationDao) {
		this.methodObservationDao = methodObservationDao;
	}

	@Inject
	public void setInvocationObservationDao(InvocationObservationDao invocationObservationDao) {
		this.invocationObservationDao = invocationObservationDao;
	}
	
	@Inject
	public void setHeapObservationDao(HeapObservationDao heapObservationDao) {
		this.heapObservationDao = heapObservationDao;
	}

	/**
	 * Creates a new instance and obtains a data source and an instance of the
	 * {@link DimensionCacheManagerImpl}
	 */
	@Inject
	public DeputoServiceImpl(DimensionCacheManager dimensionCacheManager) {
		this.dimensionCacheManager = dimensionCacheManager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #addMethodObservations(List)
	 */
	public void addObservation(Observation observation) {
		// Reduces the granularity of the observations. This also has
		// implications
		// for our internal caching. I.e. the lower granularity, the fewer
		// entries in
		// the cache.
		// Perhaps this should be configurable?
		TimeStampHelper.modifyTimeStampGranularity(observation.getTimeStamp());
		ObservationType observationType = ObservationType.valueOf(observation.getTypeName());
		switch (observationType) {
		case METHOD_OBSERVATION:
			methodObservationDao.add((MethodObservation) observation);
			break;
		case METHOD_INVOCATION:
			invocationObservationDao.add((InvocationObservation)observation);
			break;
		case HEAP:
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public void addMethodObservations(List<MethodObservation> methodObservations) {
		adjustTimeStamps(methodObservations);

		long start = System.currentTimeMillis();
		methodObservationDao.addMethodObservations(methodObservations);
		long elapsed = System.currentTimeMillis() - start;
		
		float timePerEntry = elapsed / methodObservations.size();
		log.info("Inserted " + methodObservations.size() + " entries in " + elapsed + "ms, -> " + timePerEntry + " ms/observation");
		log.info("Total elapsed time in methodObservationDao:" + methodObservationDao.getElapsedTime() + "ms for " + methodObservationDao.getInsertCounter() + " observations");
		
		if (methodObservationDao.getInsertCounter() > 0) {
			float avgTimePerInsert = ((float) methodObservationDao.getElapsedTime()) / ((float) methodObservationDao.getInsertCounter());
			log.info("Average " + avgTimePerInsert + "ms per inserted entry in dao");
		}
	}

	/**
	 * Adjusts the granularity of the time stamps of each observation. 
	 * 
	 * @param observations
	 */
	protected void adjustTimeStamps(List<? extends Observation> observations) {
		// Adjusts the time stamps
		for (Observation observation : observations) {
			TimeStampHelper.modifyTimeStampGranularity(observation.getTimeStamp());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addInvocationObservations(List<InvocationObservation> invocationObservationList) {
		adjustTimeStamps(invocationObservationList);
		
		invocationObservationDao.addInvocationObservations(invocationObservationList);
		log.info("Total elapsed time in dao for invocationObservations:" + invocationObservationDao.getElapsedTime());
		log.info("Number of inserted entries in invocationObservationDao:  " + invocationObservationDao.getInsertCounter());
		
		if (invocationObservationDao.getInsertCounter() > 0) {
			float avgTimePerInsert = ((float) invocationObservationDao.getElapsedTime()) / ((float) invocationObservationDao.getInsertCounter());
			log.info("Average " + avgTimePerInsert + " per inserted entry in invocationObservationDao");
		}
	}

	/** 
	 * {@inheritDoc}
	 */
	public void addHeapObservations(List<HeapObservation> list) {
		adjustTimeStamps(list);
		heapObservationDao.addHeapObservations(list);
		
	}

	/** {@inheritDoc} */
	public float getCacheHitRatio() {
		int requests = dimensionCacheManager.getRequests();
		int cacheHits = dimensionCacheManager.getCacheHits();
		if (requests > 0)
			return (float)cacheHits / (float)requests;
		else
			return 0.0f;
	}

	/** {@inheritDoc} */
	public int getCacheHits() {
		return dimensionCacheManager.getCacheHits();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	@Inject
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DimensionCacheManager getDimensionCacheManager() {
		return dimensionCacheManager;
	}

	@Inject
	public void setDimensionCacheManager(DimensionCacheManager dimensionCacheManager) {
		this.dimensionCacheManager = dimensionCacheManager;
	}

	public Date getLastMethodObservation() {
		Date lastObservation = methodObservationDao.getLastMethodObservation();
		return lastObservation;
	}
}
