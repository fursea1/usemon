package org.usemon.collector;

/**
 * Managed Bean interface for the collector. This Interface contains accessors
 * to the exposed attributes
 * 
 * @author t514257
 * 
 */
public interface CollectorMBean {

	/**
	 * Number of messages received from the agent This number holds no relation
	 * to the number of persisted records.
	 * 
	 * @return
	 */
	public long getNumberOfReceivedUsageMessages();

	public long getNumberOfQueuedUsageMessages();

	/**
	 * Number of persisted methodObservations
	 * 
	 * @return
	 */
	public long getNumberOfPersistedMethodObservations();

	/**
	 * Number of persisted invocation observation
	 * 
	 * @return
	 */
	public long getNumberOfPersistedInvocationObservations();

	/**
	 * number of persisted heap observations
	 * 
	 * @return
	 */
	public long getNumberOfPersistedHeapObservations();

	/**
	 * 
	 * @return
	 */
	public long getTotalNumberofPersistedObservations();


	/**
	 * Method sets all db write operations on hold or resume All messages will
	 * be queued if the operations are paused
	 * 
	 * @param pause
	 *            true to hold writing, false to resume
	 */
	public void pausePersistenceOperations();

	public void resumePersistenceOperations();

	/**
	 * pause persistence status
	 * 
	 * @return
	 */
	public boolean isPausePersistenceOperations();

	/**
	 * 
	 * @return number of seconds to hold if all message queues are empty
	 */
	public int getSleepInterval();

	/**
	 * 
	 * @param seconds
	 *            the number of seconds for the collector to sleep before
	 *            checking the message queues.
	 */
	public void setSleepInterval(int seconds);

	/** Number of hits in the cache when looking for dimension entries */
	public int getCacheHits();

	/** The ratio of cache hits to the number of insert requests */
	public float getCacheHitRatio();
	
	/** Reset all attributes to default values */
	public void resetValues();

	/** Gets threshold value for queue of objects eligible for flush to DBMS */
	public int getQueueFlushThreshold();

	/** Sets threshold value for queue of objects eligible for flush to DBMS */
	public void setQueueFlushThreshold(int queueFlushThreshold);

	public void suspendDbmsFlushingAndDropData();

	public void resumeDbmsFlushingAndPersistData();

	public int getSkippedUsageObjects();

	public float getFreeMemorySkippingThreshold();

	public void setFreeMemorySkippingThreshold(float freeMemorySkippingThreshold);

}
