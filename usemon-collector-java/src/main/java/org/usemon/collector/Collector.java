package org.usemon.collector;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.DeputoService;
import org.usemon.domain.HeapObservation;
import org.usemon.domain.InvocationObservation;
import org.usemon.domain.MethodObservation;
import org.usemon.multicast.MulticastProcessor;
import org.usemon.usageinfo.Usage;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Receives multicasted {@link Usage} objects, decodes and transforms them after which they are shoved into the DBMS.
 * 
 * <p>
 * This implementation is multi threaded. There are several multi cast receiver threads which will receive the data and places them
 * onto a main input queue, which the main thread will read in order to transform and persist the observations.
 * </p>
 * 
 * <p><strong>NOTE!</strong> the queues are not thread safe, if you are going to use more than one 
 * multi cast client per type, you must modify the code in this file.
 * </p>
 * @author Paul Rene Jørgensen
 * @author t547116 - Steinar Overbeck Cook
 * 
 */
public class Collector implements Runnable, CollectorMBean, MulticastProcessor {
	private static final Logger log = LoggerFactory.getLogger(Collector.class);
	private static final int DEFAULT_SLEEP_INTERVAL = 20;
	private static final String COLLECTOR_MBEAN_NAME = "org.usemon.collector:type=Collector";

	protected static final String PREFIX_METHOD_OBSERVATION = "methodObservation";
	protected static final String PREFIX_INVOCATION_OBSERVATION = "invocationObservation";
	protected static final String PREFIX_HEAP_OBSERVATION = "heapObservation";
	protected static final String TOKENIZER = "¤";
	protected static final int DEFAULT_QUEUE_THRESHOLD = 500;
	private static final float DEFAULT_FREE_MEM_THRESHOLD = 10.0f;

	// Holds received usage objects
	private List<Usage> usageObjectQueue;
	// Holds received objects received in Json format
	private List<String> jsonQueue;

	// Holds a queue of method observations eligible for insertion into the
	// database
	private List<MethodObservation> methodObservationQueue = new ArrayList<MethodObservation>();
	private List<InvocationObservation> invocationObservationQueue = new ArrayList<InvocationObservation>();
	private List<HeapObservation> heapObservationQueue = new ArrayList<HeapObservation>();

	private DeputoService deputoService;

	/*
	 * Attributes exposed in mbean
	 */
	private boolean pausePersistence = false;
	private long numberOfReceivedUsageMessages = 0;
	private long methodCounter = 0;
	private long invocationCounter = 0;
	private long heapCounter = 0;
	private int sleepInterval = DEFAULT_SLEEP_INTERVAL;
	int queueFlushThreshold = DEFAULT_QUEUE_THRESHOLD;
	int skippedUsageObjects = 0;
	public float freeMemorySkippingThreshold = 10.0f;
	
	/* ---------- */
	public boolean dbmsFlushingSuspended = false; // Whether flushing to DBMS

	private Runtime runtime;

	/**
	 * Default Constructor Initializes queues, sets up multicast configuration by creating the multicast client objects which will
	 * write their received data into the queue held in an instance of this class.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public Collector() {
		runtime = Runtime.getRuntime();
		
		registerMbean();

		usageObjectQueue = (List<Usage>) Collections.synchronizedList(new LinkedList<Usage>());
		jsonQueue = (List<String>)Collections.synchronizedList(new LinkedList<String>());
	}

	/**
	 * Performs the MBean registration
	 */
	private void registerMbean() {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		// Construct the ObjectName for the MBean we will register
		ObjectName name;
		try {
			name = new ObjectName(COLLECTOR_MBEAN_NAME);
			mbs.registerMBean(this, name);
		} catch (MalformedObjectNameException e) {
			throw new IllegalStateException("Unable to register MBean: " + e, e);
		} catch (NullPointerException e) {
			throw new IllegalStateException("Null pointer exception while registering MBean " + e, e);
		} catch (InstanceAlreadyExistsException e) {
			throw new IllegalStateException("Mbean instance already registered:" + e, e);
		} catch (MBeanRegistrationException e) {
			throw new IllegalStateException("Unable to register mbean " + e,e);
		} catch (NotCompliantMBeanException e) {
			throw new IllegalStateException("MBean not compliant " + e,e);
		}
	}
	private void unregisterMbean() {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			mbs.unregisterMBean(new ObjectName(COLLECTOR_MBEAN_NAME));
		} catch (Exception e) {
			throw new IllegalStateException("Unable to unregister MBean " + e,e);
		}
	}

	
	/**
	 * Main method of the thread. There are two separate queues into which objects are placed by multi cast cliens running in
	 * separate threads.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		log.info("Usemon Collector started");

		while (true) {
			try {
				// If the usage queue is not empty, handle the objects
				if (!usageObjectQueue.isEmpty() && !pausePersistence)
					// Picks first object, removes it from the queue and
					// transforms it into separate method observations, invocation
					// observations and a single heap observation object.
					handleUsageFact(usageObjectQueue.remove(0));

				// If any JSON messages have been received, handle them like
				// above
				if (!jsonQueue.isEmpty() && !pausePersistence)
					handleJSONMessage(jsonQueue.remove(0));
			} catch (Throwable t) {
				log.warn("Continuing after usage warning: " + t, t);
			}

			// If the input queue is empty or a flush of the output queue is
			// required
			if (isFlushRecommended())
				flushToDatabase(); // Flush to DBMS

			if (usageObjectQueue.isEmpty() || pausePersistence) {
				try {
					if (pausePersistence == true) {
						log.info("Persistence Paused, messages are queued, while sleeping for " + (sleepInterval) + "ms");
					}
					Thread.sleep(sleepInterval);
				} catch (InterruptedException e) {
					log.warn("Interrupted, collector terminating, there are " + usageObjectQueue.size() + " entries left in the queue");
					unregisterMbean();
					return;
				}
			}
		}
	}


	/** Determines whether it is recommended to flush the output queues to the DBMS or not. */
	private boolean isFlushRecommended() {
		boolean flushRequired = false;
		if (isInputQueueEmptyAndOutputQueueContainsData() || hasOuputQueuesExceededThreshold()) {
			flushRequired = true;
		}
		return flushRequired;
	}

	/**
	 * Indicates whether any of the output queues have exceeded their threshold and should be flushed to disk.
	 * 
	 * @return <code>true</code> if any of the output queues have exceeded their threshold value, <code>false</code> otherwise
	 */
	private boolean hasOuputQueuesExceededThreshold() {
		boolean result = (methodObservationQueue.size() >= queueFlushThreshold 
				|| invocationObservationQueue.size() >= queueFlushThreshold 
				|| heapObservationQueue.size() >= queueFlushThreshold);
		
		return result;
	}

	/**
	 * Indicates whether the input queue is empty and any of the output queues contains data, which means that flushing is a good
	 * idea.
	 * 
	 * @return <code>true</code> if the input queue is empty and either of the output queues contains data, <code>false</code>
	 *         otherwise
	 */
	private boolean isInputQueueEmptyAndOutputQueueContainsData() {
		boolean result = usageObjectQueue.isEmpty()
				&& (!methodObservationQueue.isEmpty() || !invocationObservationQueue.isEmpty() || !heapObservationQueue.isEmpty());
		return result;
	}

	private void flushToDatabase() {
		log.info("Flushing received data to the database");

		// Flushes the method observation queue only if it is not empty
		if (log.isDebugEnabled()) {
			log.debug("methodObservationQueue.isEmpty: " + methodObservationQueue.isEmpty());
		}
		if (!methodObservationQueue.isEmpty()) {
			if (!isDbmsFlushingSuspended()) {
				if (log.isDebugEnabled()) {
					log.debug("Flushing " + methodObservationQueue.size() + " objects to the database");
				}
				deputoService.addMethodObservations(methodObservationQueue);
				methodCounter += methodObservationQueue.size();
				if (log.isDebugEnabled()) {
					log.debug("Flushed " + methodObservationQueue.size() + " method observations to DBMS, accumulated total is " + methodCounter);
				}
			}
			methodObservationQueue.clear();
		}

		// Flushes the queue of invocation objects only if it is not empty
		if (log.isDebugEnabled()) {
			log.debug("invocationObservationQueue.isEmpty() :- " + invocationObservationQueue.isEmpty());
		}
		if (!invocationObservationQueue.isEmpty()) {
			if (!isDbmsFlushingSuspended()) {
				deputoService.addInvocationObservations(invocationObservationQueue);
				invocationCounter += invocationObservationQueue.size();
				if (log.isDebugEnabled()) {
					log.debug("Flushed " + invocationObservationQueue.size() + " invocation observations to DBMS, accumulated total is "
							+ invocationCounter);
				}
				invocationObservationQueue.clear();
			}
		}

		// Flushes the queue of heap observations if it is not empty
		if (log.isDebugEnabled()) {
			log.debug("heapObservationQueue.isEmpty() :- " + heapObservationQueue.isEmpty());
		}
		if (!heapObservationQueue.isEmpty()) {
			if (!isDbmsFlushingSuspended()) {
				// deputoService.addHeapObservations(heapObservationQueue);
				heapCounter += heapObservationQueue.size();
				if (log.isDebugEnabled()) {
					log.debug("Flushed " + heapObservationQueue.size() + " heap observations to DBMS, accumulated total is " + heapCounter);
				}
				heapObservationQueue.clear();
			}
		}
	}

	/**
	 * Decode message, create java object and store observation
	 * 
	 * @param message -
	 *            prefixed J message.
	 */
	protected void handleJSONMessage(String message) {
		/*
		 * Determine type of object by removing and analyzin prefix
		 * 
		 */
		String[] tokenized = message.split(TOKENIZER);
		if (tokenized.length == 2) {
			String objectType = tokenized[0];
			String jsonMessage = tokenized[1];
			if (PREFIX_METHOD_OBSERVATION.equalsIgnoreCase(objectType)) {
				MethodObservation observation = ObservationMappingUtils.buildMethodObservationFromJSONMessage(jsonMessage);
				deputoService.addObservation(observation);
			} else if (PREFIX_INVOCATION_OBSERVATION.equalsIgnoreCase(objectType)) {
				InvocationObservation observation = ObservationMappingUtils.buildInvocationObservationFromJSONMessage(jsonMessage);
				deputoService.addObservation(observation);
			} else if (PREFIX_HEAP_OBSERVATION.equalsIgnoreCase(objectType)) {
				HeapObservation observation = ObservationMappingUtils.buildHeapObservationFromJSONMessage(jsonMessage);
				deputoService.addObservation(observation);

			}
		} else {
			log.error("Tried to handle illegal JSON message. Message either contains illegal characters or is in wrong format: " + message);
		}

	}

	/**
	 * Transforms a single Usage object into MethodObservation, InvocationObservation and HeapObservation objects, after which they
	 * are placed onto their global dbms output queues.
	 * 
	 * @param usage -
	 *            de-serialized Usage object
	 */
	protected void handleUsageFact(Usage usage) {
		if (usage != null) {

			// Split the usage object into several separate method observations
			// per principal
			List<MethodObservation> methodObservationList = ObservationMappingUtils.buildMethodObservationFromMethodUsage(usage);
			assert methodObservationList != null : "null value should never be returned from buildMethodObservationFromMethodUsage()";
			if (!methodObservationList.isEmpty()) {
				if (log.isDebugEnabled()) {
					log.debug("Added " + methodObservationList.size() + " method observations to dbms queue");
				}
				methodObservationQueue.addAll(methodObservationList);
			}

			// Splits the usage object into several invocation observation
			// objects
			List<InvocationObservation> invocationList = ObservationMappingUtils.buildInvocationsFromMethodUsage(usage);
			if (invocationList != null && invocationList.size() > 0) {
				invocationObservationQueue.addAll(invocationList);
			}

			// finally, extracts the heap objects and places them onto the dbms
			// output queue
			heapObservationQueue.add(ObservationMappingUtils.buildHeapObservationFromUsage(usage));
		}
	}

	public long getNumberOfPersistedInvocationObservations() {
		return invocationCounter;
	}

	public long getNumberOfPersistedMethodObservations() {
		return methodCounter;
	}

	public long getNumberOfReceivedUsageMessages() {
		return numberOfReceivedUsageMessages;
	}

	public void pausePersistenceOperations() {
		pausePersistence = true;

	}

	public void resumePersistenceOperations() {
		pausePersistence = false;

	}

	public boolean isPausePersistenceOperations() {
		return pausePersistence;
	}

	public int getSleepInterval() {
		return sleepInterval;
	}

	public void setSleepInterval(int seconds) {
		sleepInterval = seconds;
	}

	public long getNumberOfPersistedHeapObservations() {
		return heapCounter;
	}

	public long getTotalNumberofPersistedObservations() {
		return heapCounter + methodCounter + invocationCounter;
	}

	public long getNumberOfQueuedUsageMessages() {
		return usageObjectQueue.size();
	}

	public float getCacheHitRatio() {
		return deputoService.getCacheHitRatio();
	}

	public int getCacheHits() {
		return deputoService.getCacheHits();
	}

	public void resetValues() {
		sleepInterval = DEFAULT_SLEEP_INTERVAL;
		pausePersistence = false;
		numberOfReceivedUsageMessages = 0;
		methodCounter = 0;
		invocationCounter = 0;
		heapCounter = 0;
		queueFlushThreshold = DEFAULT_QUEUE_THRESHOLD;
		freeMemorySkippingThreshold = DEFAULT_FREE_MEM_THRESHOLD;
	}

	public int getQueueFlushThreshold() {
		return queueFlushThreshold;
	}

	public void setQueueFlushThreshold(int queueFlushThreshold) {
		this.queueFlushThreshold = queueFlushThreshold;
	}

	public boolean isDbmsFlushingSuspended() {
		return dbmsFlushingSuspended;
	}

	public void setSuspendDbmsFlush(boolean suspendDbmsFlush) {
		this.dbmsFlushingSuspended = suspendDbmsFlush;
	}

	public void suspendDbmsFlushingAndDropData() {
		log.warn("DBMS Flushing suspended");
		dbmsFlushingSuspended = true;
	}

	public void resumeDbmsFlushingAndPersistData() {
		log.warn("DBMS FLushing resumed");
		dbmsFlushingSuspended = false;
	}

	protected DeputoService getDeputoService() {
		return deputoService;
	}

	protected void setDeputoService(DeputoService deputoService) {
		this.deputoService = deputoService;
	}

	/** Adds another message to the queue of Json messages if there is sufficient
	 * memory available.
	 */
	public void addJsonMessage(String message) {
		if (isSufficientMemoryAvailable())
			jsonQueue.add(message);
	}

	/**
	 * Adds another message to the Java Usage queue if there is sufficient memory
	 * available.
	 */
	public void addUsageMessage(Usage usage) {
		if (isSufficientMemoryAvailable()) {
			usageObjectQueue.add(usage);
			numberOfReceivedUsageMessages++;
		}
		else
			skippedUsageObjects++;
	}
	
	protected boolean isSufficientMemoryAvailable() {
		float avail = memoryAvailableAsPercentage();
		if (avail < freeMemorySkippingThreshold)
			return false;
		else
			return true;
	}

	/**
	 * Calculates how much memory is available as a percentage 
	 */
	protected float memoryAvailableAsPercentage() {
		float max = runtime.maxMemory();
		float available = runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory();
		float availablePct = (available / max) * 100.0f;
		return availablePct;
	}

	/** Provides the number of Usage objects skipped due to memory constraints */
	public int getSkippedUsageObjects() {
		return skippedUsageObjects;
	}

	/** Free memory percentage threshold for skipping usage objects.
	 * I.e. if free memory falls below this threshold, usage objects are skipped 
	 */
	public float getFreeMemorySkippingThreshold() {
		return freeMemorySkippingThreshold;
	}

	/** @see #getFreeMemorySkippingThreshold */
	public void setFreeMemorySkippingThreshold(float freeMemorySkippingThreshold) {
		this.freeMemorySkippingThreshold = freeMemorySkippingThreshold;
	}
}